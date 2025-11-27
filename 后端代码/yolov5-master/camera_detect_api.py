import cv2
import torch
import threading
import time
import os
import numpy as np
import sys
from flask import Flask, jsonify, send_from_directory, request, Response, abort

sys.path.append(os.path.abspath('.'))  # 保证能import yolov5-master下的模块
from models.experimental import attempt_load
from utils.general import non_max_suppression, scale_boxes

try:
    from onvif import ONVIFCamera
except ImportError:
    ONVIFCamera = None

app = Flask(__name__)

# 目录配置
RAW_VIDEO_DIR = os.path.abspath('./raw_videos')
DETECT_VIDEO_DIR = os.path.abspath('./detect_videos')
MODEL_DIR = os.path.abspath('./model')
os.makedirs(RAW_VIDEO_DIR, exist_ok=True)
os.makedirs(DETECT_VIDEO_DIR, exist_ok=True)

# 全局流控制标志，支持多摄像头
streaming_flags = {}
# 自动保存检测的全局停止标志，支持多摄像头
stop_flags = {}

def detect_camera(idx, model_path, frame_interval=1):
    if isinstance(idx, int) or (isinstance(idx, str) and idx.isdigit()):
        cap = cv2.VideoCapture(int(idx))
    else:
        cap = cv2.VideoCapture(idx, cv2.CAP_DSHOW)
    model = attempt_load(model_path, device='cpu')
    fourcc = cv2.VideoWriter_fourcc(*'avc1')  # H.264
    timestamp = time.strftime('%Y%m%d_%H%M%S')
    raw_video_path = os.path.join(RAW_VIDEO_DIR, f'camera{idx}_{timestamp}_raw.mp4')
    detect_video_path = os.path.join(DETECT_VIDEO_DIR, f'camera{idx}_{timestamp}_detect.mp4')
    out_raw = cv2.VideoWriter(raw_video_path, fourcc, 20.0, (int(cap.get(3)), int(cap.get(4))))
    out_detect = cv2.VideoWriter(detect_video_path, fourcc, 20.0, (int(cap.get(3)), int(cap.get(4))))
    stop_flags[idx] = False  # 启动时标志为False
    frame_id = 0
    while not stop_flags.get(idx, False):
        ret, frame = cap.read()
        if not ret:
            break
        frame_id += 1
        out_raw.write(frame)
        if frame_interval > 1 and (frame_id - 1) % frame_interval != 0:
            out_detect.write(frame)
            continue
        # 推理
        img = cv2.resize(frame, (640, 640))
        img = img.transpose((2, 0, 1))[::-1]
        img = np.ascontiguousarray(img)
        img = torch.from_numpy(img).to('cpu').float() / 255.0
        if img.ndimension() == 3:
            img = img.unsqueeze(0)
        pred = model(img)[0]
        pred = non_max_suppression(pred, 0.25, 0.45)
        annotated_frame = frame.copy()
        for i, det in enumerate(pred):
            if det is not None and len(det):
                det[:, :4] = scale_boxes(img.shape[2:], det[:, :4], frame.shape).round()
                for *xyxy, conf, cls in det:
                    x1, y1, x2, y2 = map(int, xyxy)
                    class_id = int(cls)
                    confidence = float(conf)
                    color = (0, 255, 0)
                    cv2.rectangle(annotated_frame, (x1, y1), (x2, y2), color, 2)
                    label = f"{class_id} {confidence:.2f}"
                    label_size = cv2.getTextSize(label, cv2.FONT_HERSHEY_SIMPLEX, 0.5, 2)[0]
                    cv2.rectangle(annotated_frame, (x1, y1 - label_size[1] - 10), (x1 + label_size[0], y1), color, -1)
                    cv2.putText(annotated_frame, label, (x1, y1 - 5), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2)
        out_detect.write(annotated_frame)
    cap.release()
    out_raw.release()
    out_detect.release()

@app.route('/api/camera/start', methods=['POST'])
def start_camera():
    data = request.json
    camera_source = data.get('camera_source')
    idx = data.get('camera_idx', 0) if camera_source is None else camera_source
    model_file = data.get('model', 'your_model.pt')
    model_path = os.path.join(MODEL_DIR, model_file)
    frame_interval = data.get('frame_interval', 1)
    stop_flags[idx] = False  # 启动时重置停止标志
    detect_camera(idx, model_path, frame_interval)
    return jsonify({'success': True, 'message': f'摄像头{idx}检测已完成'})

@app.route('/api/camera/stop', methods=['POST'])
def stop_camera():
    data = request.json or {}
    idx = data.get('camera_idx', 0)
    stop_flags[idx] = True  # 停止标志设为True，detect_camera会自动退出
    return jsonify({'success': True, 'message': '检测已停止'})

@app.route('/api/camera/status', methods=['GET'])
def camera_status_api():
    return jsonify({}) # 移除多线程状态

@app.route('/api/camera/list', methods=['GET'])
def list_cameras():
    available = []
    for idx in range(10):
        cap = cv2.VideoCapture(idx)
        if cap.isOpened():
            available.append(str(idx))
            cap.release()
    return jsonify(available)

@app.route('/api/camera/stream')
def video_feed():
    model_file = request.args.get('model', 'your_model.pt')
    model_path = os.path.join(MODEL_DIR, model_file)
    camera_idx = request.args.get('camera_idx', 0)
    try:
        camera_idx = int(camera_idx)
    except Exception:
        camera_idx = 0
    frame_interval = int(request.args.get('frame_interval', 1))
    streaming_flags[camera_idx] = True
    def gen_frames(camera_idx):
        frame_id = 0
        cap = cv2.VideoCapture(camera_idx)
        model = attempt_load(model_path, device='cpu')
        saved = False
        while streaming_flags.get(camera_idx, True):
            ret, frame = cap.read()
            if not ret:
                break
            frame_id += 1
            if frame_interval > 1 and (frame_id - 1) % frame_interval != 0:
                # 跳过检测，直接推流原始帧
                _, buffer = cv2.imencode('.jpg', frame)
                frame_bytes = buffer.tobytes()
                yield (b'--frame\r\n'
                       b'Content-Type: image/jpeg\r\n\r\n' + frame_bytes + b'\r\n')
                continue
            # 检测逻辑
            img = cv2.resize(frame, (640, 640))
            img = img.transpose((2, 0, 1))[::-1]
            img = np.ascontiguousarray(img)
            img = torch.from_numpy(img).to('cpu').float() / 255.0
            if img.ndimension() == 3:
                img = img.unsqueeze(0)
            pred = model(img)[0]
            pred = non_max_suppression(pred, 0.25, 0.45)
            annotated_frame = frame.copy()
            for i, det in enumerate(pred):
                if det is not None and len(det):
                    det[:, :4] = scale_boxes(img.shape[2:], det[:, :4], frame.shape).round()
                    for *xyxy, conf, cls in det:
                        x1, y1, x2, y2 = map(int, xyxy)
                        class_id = int(cls)
                        confidence = float(conf)
                        color = (0, 255, 0)
                        cv2.rectangle(annotated_frame, (x1, y1), (x2, y2), color, 2)
                        label = f"{class_id} {confidence:.2f}"
                        label_size = cv2.getTextSize(label, cv2.FONT_HERSHEY_SIMPLEX, 0.5, 2)[0]
                        cv2.rectangle(annotated_frame, (x1, y1 - label_size[1] - 10), (x1 + label_size[0], y1), color, -1)
                        cv2.putText(annotated_frame, label, (x1, y1 - 5), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2)
            _, buffer = cv2.imencode('.jpg', annotated_frame)
            frame_bytes = buffer.tobytes()
            yield (b'--frame\r\n'
                   b'Content-Type: image/jpeg\r\n\r\n' + frame_bytes + b'\r\n')
        cap.release()
    return Response(gen_frames(camera_idx), mimetype='multipart/x-mixed-replace; boundary=frame')

@app.route('/api/camera/stream/stop', methods=['POST'])
def stop_camera_stream():
    """
    显式释放摄像头流资源（由 Spring Boot 转发调用）
    支持多摄像头，需传 camera_idx 标识。
    """
    data = request.json or {}
    camera_idx = data.get('camera_idx', 0)
    try:
        camera_idx = int(camera_idx)
    except Exception:
        camera_idx = 0
    streaming_flags[camera_idx] = False
    return jsonify({'success': True, 'message': f'摄像头{camera_idx}流已停止/资源已释放'})

@app.route('/api/camera/auto_detect_rtsp', methods=['POST'])
def auto_detect_rtsp():
    data = request.json
    ip = data.get('ip')
    user = data.get('user')
    pwd = data.get('pwd')
    port_raw = data.get('port', 80)
    try:
        port = int(port_raw) if str(port_raw).strip() else 80
    except Exception:
        port = 80
    # 检查缺失字段
    missing = []
    if not ip:
        missing.append('ip')
    if not user:
        missing.append('user')
    if not pwd:
        missing.append('pwd')
    if missing:
        return jsonify({'success': False, 'error': f"参数缺失: {', '.join(missing)}"}), 400
    if ONVIFCamera is None:
        return jsonify({'success': False, 'error': 'onvif模块未安装，请pip install onvif-zeep'}), 500
    try:
        mycam = ONVIFCamera(ip, port, user, pwd)
        media_service = mycam.create_media_service()
        profiles = media_service.GetProfiles()
        uris = []
        for profile in profiles:
            token = profile.token
            uri = media_service.GetStreamUri({'StreamSetup': {'Stream': 'RTP-Unicast', 'Transport': {'Protocol': 'RTSP'}}, 'ProfileToken': token})
            uris.append({'profile': profile.Name, 'uri': uri.Uri})
        return jsonify({'success': True, 'rtsp_list': uris})
    except Exception as e:
        return jsonify({'success': False, 'error': str(e)}), 500

@app.route('/api/videos/raw', methods=['GET'])
def list_raw_videos():
    files = os.listdir(RAW_VIDEO_DIR)
    return jsonify(sorted(files))

@app.route('/api/videos/detect', methods=['GET'])
def list_detect_videos():
    files = os.listdir(DETECT_VIDEO_DIR)
    return jsonify(sorted(files))

@app.route('/api/videos/raw/<filename>')
def download_raw_video(filename):
    return send_from_directory(RAW_VIDEO_DIR, filename)

@app.route('/api/videos/detect/<filename>')
def download_detect_video(filename):
    return send_from_directory(DETECT_VIDEO_DIR, filename)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5001, debug=True)
