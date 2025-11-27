import os
import torch
import cv2
import numpy as np
from flask import Flask, request, jsonify, render_template, send_file
from PIL import Image, ImageFont, ImageDraw
import base64
import io
import json
from datetime import datetime
import logging
import sys
import tempfile
import subprocess
import zipfile
import time
from utils.general import scale_boxes

sys.path.append('yolov5-master')  # 相对于程序运行的当前工作目录的，而不是相对于该代码所在文件的目录

# 配置日志
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)

# 配置路径
MODEL_FOLDER = 'model'
TESTPIC_FOLDER = 'testpic'
RESULTS_FOLDER = 'results'
UPLOAD_FOLDER = 'uploads'
os.makedirs(UPLOAD_FOLDER, exist_ok=True)

# 确保结果目录存在
os.makedirs(RESULTS_FOLDER, exist_ok=True)

# 全局变量存储当前模型
current_model = None
model_info = {}


class YOLOv5Detector:
    def __init__(self):
        self.model = None
        self.model_path = None
        # 检查字体文件
        self.font_path = "simhei.ttf"
        if not os.path.exists(self.font_path):
            logger.warning(f"未找到中文字体文件: {self.font_path}，中文标签将无法正常显示！")
            self.font_path = None

    def load_model(self, model_path):
        """加载YOLOv5模型"""
        try:
            # 使用本地YOLOv5代码加载模型
            import sys
            # sys.path.append('yolov5-master')
            from models.experimental import attempt_load

            self.model = attempt_load(model_path, device='cpu')
            self.model_path = model_path
            logger.info(f"模型加载成功: {model_path}")
            return True
        except Exception as e:
            logger.error(f"模型加载失败: {str(e)}")
            return False

    def detect_image_base64(self, image_base64, conf_threshold=0.25, iou_threshold=0.45):
        """检测base64格式的图片"""
        try:
            if self.model is None:
                return {"error": "模型未加载"}

            # 导入本地YOLOv5的检测函数
            import sys
            # sys.path.append('yolov5-master')
            from utils.general import non_max_suppression
            from utils.torch_utils import select_device
            import cv2
            import numpy as np

            # 解码base64图片
            try:
                # 移除可能的data:image/jpeg;base64,前缀
                if ',' in image_base64:
                    image_base64 = image_base64.split(',')[1]

                image_data = base64.b64decode(image_base64)
                nparr = np.frombuffer(image_data, np.uint8)
                img0 = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

                if img0 is None:
                    return {"error": "无法解码base64图片"}
            except Exception as e:
                return {"error": f"base64解码失败: {str(e)}"}

            # 设置设备
            device = select_device('cpu')

            # 预处理图片
            img = cv2.resize(img0, (640, 640))
            img = img.transpose((2, 0, 1))[::-1]  # HWC to CHW, BGR to RGB
            img = np.ascontiguousarray(img)  # 确保内存连续
            img = torch.from_numpy(img).to(device)  # 转为Tensor并移至目标设备
            img = img.float()
            img /= 255.0  # 归一化
            if img.ndimension() == 3:  # 用于获取张量的维度数量
                img = img.unsqueeze(0)  # 增加一个维度，用于batch处理

            # 推理
            pred = self.model(img)[0]
            pred = non_max_suppression(pred, conf_threshold, iou_threshold)

            # 处理结果
            detections = []
            annotated_img = img0.copy()
            # 用于PIL写字
            use_pil = self.font_path is not None
            if use_pil:
                annotated_img_pil = Image.fromarray(cv2.cvtColor(annotated_img, cv2.COLOR_BGR2RGB))
                draw = ImageDraw.Draw(annotated_img_pil)
                font = ImageFont.truetype(self.font_path, 20)

            for i, det in enumerate(pred):
                if det is not None and len(det):
                    det[:, :4] = scale_boxes(img.shape[2:], det[:, :4], img0.shape).round()  # 将边界框坐标映射回原始图片大小
                    for *xyxy, conf, cls in det:  # det: [x1, y1, x2, y2, conf, cls] xyxy: [x1, y1, x2, y2] conf: 置信度   cls: 类别ID
                        x1, y1, x2, y2 = map(int, xyxy)
                        class_id = int(cls)
                        class_name = self.model.names[class_id]
                        confidence = float(conf)
                        color = (0, 255, 0)
                        # 1. 先用OpenCV画框
                        cv2.rectangle(annotated_img, (x1, y1), (x2, y2), color, 2)
                        label = f"{class_name} {confidence:.2f}"
                        if use_pil:
                            # 2. 用PIL写字（只写字）
                            annotated_img_pil = Image.fromarray(
                                cv2.cvtColor(annotated_img, cv2.COLOR_BGR2RGB))  # PIL只能处理RGB格式
                            draw = ImageDraw.Draw(annotated_img_pil)  # PIL的draw对象
                            draw.text((x1, y1 - 25), label, font=font, fill=(0, 255, 0))  # PIL写中文需要用RGB格式
                            annotated_img = cv2.cvtColor(np.array(annotated_img_pil),
                                                         cv2.COLOR_RGB2BGR)  # PIL写完中文后需要转回BGR格式
                        else:
                            # OpenCV写英文
                            label_size = cv2.getTextSize(label, cv2.FONT_HERSHEY_SIMPLEX, 0.5, 2)[0]
                            cv2.rectangle(annotated_img, (x1, y1 - label_size[1] - 10), (x1 + label_size[0], y1), color,
                                          -1)
                            cv2.putText(annotated_img, label, (x1, y1 - 5), cv2.FONT_HERSHEY_SIMPLEX, 0.5,
                                        (255, 255, 255), 2)
                        detection = {
                            "bbox": [x1, y1, x2, y2],
                            "confidence": confidence,
                            "class": class_id,
                            "class_name": class_name
                        }
                        detections.append(detection)

            # 将标注后的图片转换为base64
            try:
                _, buffer = cv2.imencode('.jpg', annotated_img)  # 将OpenCV格式图片编码为 JPEG 格式的二进制数据
                annotated_base64 = base64.b64encode(buffer).decode('utf-8')  # 二进制数据——> Base64格式的字节流——> base64编码的字符串
            except Exception as e:
                return {"error": f"图片编码失败: {str(e)}"}

            return {
                "success": True,
                "detections": detections,
                "total_detections": len(detections),
                "annotated_image_base64": annotated_base64,
                "original_image_shape": img0.shape
            }

        except Exception as e:
            logger.error(f"检测失败: {str(e)}")
            return {"error": f"检测失败: {str(e)}"}

    def detect_video_base64(self, video_base64, model_name, conf_threshold=0.25, iou_threshold=0.45, frame_interval=1):
        """检测base64格式的视频"""
        try:
            if self.model is None:
                return {"error": "模型未加载"}

            # 导入本地YOLOv5的检测函数
            import sys
            sys.path.append('yolov5-master')
            from utils.general import non_max_suppression
            from utils.torch_utils import select_device
            import cv2
            import numpy as np

            # 解码base64视频
            try:
                # 移除可能的data:video/mp4;base64,前缀
                if ',' in video_base64:
                    video_base64 = video_base64.split(',')[1]

                video_data = base64.b64decode(video_base64)

                # 创建临时文件保存视频
                with tempfile.NamedTemporaryFile(suffix='.mp4', delete=False) as temp_video:
                    temp_video.write(video_data)
                    temp_video_path = temp_video.name

                # 创建临时文件保存输出视频
                with tempfile.NamedTemporaryFile(suffix='.mp4', delete=False) as temp_output:
                    temp_output_path = temp_output.name

            except Exception as e:
                return {"error": f"base64解码失败: {str(e)}"}

            try:
                # 打开视频文件
                cap = cv2.VideoCapture(temp_video_path)
                if not cap.isOpened():
                    return {"error": "无法打开视频文件"}

                # 获取视频信息
                fps = int(cap.get(cv2.CAP_PROP_FPS))  # 获取视频帧率
                width = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))  # 获取视频宽度
                height = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))  # 获取视频高度
                total_frames = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))  # 获取视频总帧数

                # 设置设备
                device = select_device('cpu')

                # 创建视频写入器
                fourcc = cv2.VideoWriter_fourcc(*'mp4v')  # 设置视频编码格式
                out = cv2.VideoWriter(temp_output_path, fourcc, fps, (width, height))  # 设置视频帧率、尺寸

                frame_count = 0
                total_detections = 0

                while True:
                    ret, frame = cap.read()
                    if not ret:
                        break

                    frame_count += 1
                    if frame_interval > 1 and (frame_count - 1) % frame_interval != 0:
                        out.write(frame)
                        continue

                    logger.info(f"处理第 {frame_count}/{total_frames} 帧")

                    # 预处理图片
                    img = cv2.resize(frame, (640, 640))
                    img = img.transpose((2, 0, 1))[::-1]  # HWC to CHW, BGR to RGB
                    img = np.ascontiguousarray(img)  # 转换为连续数组
                    img = torch.from_numpy(img).to(device)
                    img = img.float()
                    img /= 255.0
                    if img.ndimension() == 3:
                        img = img.unsqueeze(0)

                    # 推理
                    pred = self.model(img)[0]
                    pred = non_max_suppression(pred, conf_threshold, iou_threshold)

                    # 处理结果
                    annotated_frame = frame.copy()

                    for i, det in enumerate(pred):
                        if det is not None and len(det):
                            # 缩放坐标到原图尺寸
                            det[:, :4] = scale_boxes(img.shape[2:], det[:, :4], frame.shape).round()

                            for *xyxy, conf, cls in det:
                                x1, y1, x2, y2 = map(int, xyxy)
                                class_id = int(cls)
                                class_name = self.model.names[class_id]
                                confidence = float(conf)

                                # 绘制边界框
                                color = (0, 255, 0)  # 绿色
                                cv2.rectangle(annotated_frame, (x1, y1), (x2, y2), color, 2)

                                # 绘制标签
                                label = f"{class_name} {confidence:.2f}"
                                label_size = cv2.getTextSize(label, cv2.FONT_HERSHEY_SIMPLEX, 0.5, 2)[0]
                                cv2.rectangle(annotated_frame, (x1, y1 - label_size[1] - 10),
                                              (x1 + label_size[0], y1), color, -1)
                                cv2.putText(annotated_frame, label, (x1, y1 - 5),
                                            cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2)

                                total_detections += 1

                    # 写入处理后的帧
                    out.write(annotated_frame)

                # 释放资源
                cap.release()
                out.release()

                # 转换为H.264格式
                h264_output_path = temp_output_path.replace('.mp4', '_h264.mp4')
                try:
                    # 使用ffmpeg转换为H.264格式
                    cmd = [
                        'ffmpeg', '-i', temp_output_path,
                        '-c:v', 'libx264',
                        '-preset', 'medium',
                        '-crf', '23',
                        '-y',  # 覆盖输出文件
                        h264_output_path
                    ]

                    result = subprocess.run(cmd, capture_output=True, text=True)
                    if result.returncode != 0:
                        logger.warning(f"FFmpeg转换警告: {result.stderr}")
                        # 如果转换失败，使用原文件
                        h264_output_path = temp_output_path

                except Exception as e:
                    logger.warning(f"FFmpeg转换失败: {str(e)}")
                    # 如果转换失败，使用原文件
                    h264_output_path = temp_output_path

                # 读取处理后的视频并转换为base64
                try:
                    with open(h264_output_path, 'rb') as video_file:
                        video_base64 = base64.b64encode(video_file.read()).decode('utf-8')
                except Exception as e:
                    return {"error": f"视频编码失败: {str(e)}"}

                # 清理临时文件
                try:
                    os.unlink(temp_video_path)
                    os.unlink(temp_output_path)
                    if h264_output_path != temp_output_path:
                        os.unlink(h264_output_path)
                except:
                    pass

                return {
                    "success": True,
                    "total_frames": frame_count,
                    "total_detections": total_detections,
                    "video_base64": video_base64,
                    "video_info": {
                        "fps": fps,
                        "width": width,
                        "height": height,
                        "duration": frame_count / fps if fps > 0 else 0
                    }
                }

            except Exception as e:
                # 清理临时文件
                try:
                    os.unlink(temp_video_path)
                    os.unlink(temp_output_path)
                except:
                    pass
                raise e

        except Exception as e:
            logger.error(f"视频检测失败: {str(e)}")
            return {"error": f"视频检测失败: {str(e)}"}


# 创建检测器实例
detector = YOLOv5Detector()


@app.route('/')
def index():
    """主页"""
    # 获取可用的模型列表
    model_files = []
    if os.path.exists(MODEL_FOLDER):
        for file in os.listdir(MODEL_FOLDER):
            if file.endswith('.pt'):
                model_files.append(file)

    # 获取可用的测试图片列表
    test_images = []
    if os.path.exists(TESTPIC_FOLDER):
        for file in os.listdir(TESTPIC_FOLDER):
            if file.lower().endswith(('.jpg', '.jpeg', '.png', '.bmp')):
                test_images.append(file)

    return render_template('index.html',
                           model_files=model_files,
                           test_images=test_images,
                           current_model=current_model)


@app.route('/api/load_model', methods=['POST'])
def load_model():
    """加载指定模型"""
    try:
        data = request.get_json()
        model_name = data.get('model_name')

        if not model_name:
            return jsonify({"error": "请提供模型名称"}), 400

        model_path = os.path.join(MODEL_FOLDER, model_name)
        if not os.path.exists(model_path):
            return jsonify({"error": f"模型文件不存在: {model_name}"}), 400

        if detector.load_model(model_path):
            global current_model, model_info
            current_model = model_name
            # 获取类别名
            class_names = []
            if detector.model and hasattr(detector.model, 'names'):
                if isinstance(detector.model.names, dict):
                    class_names = list(detector.model.names.values())
                else:
                    class_names = list(detector.model.names)
            model_info = {
                "name": model_name,
                "path": model_path,
                "load_time": datetime.now().isoformat(),
                "status": "loaded",
                "class_names": class_names
            }

            return jsonify({
                "success": True,
                "message": f"模型 {model_name} 加载成功",
                "model_info": model_info,
                "class_names": class_names
            })
        else:
            return jsonify({"error": "模型加载失败"}), 500

    except Exception as e:
        logger.error(f"模型加载失败: {str(e)}")
        return jsonify({"error": f"模型加载失败: {str(e)}"}), 500


@app.route('/api/detect_base64', methods=['POST'])
def detect_base64():
    """检测base64格式的图片"""
    try:
        data = request.get_json()
        image_base64 = data.get('image_base64')
        model_name = data.get('model_name')
        conf_threshold = data.get('conf_threshold', 0.25)
        iou_threshold = data.get('iou_threshold', 0.45)
        target_class_names = data.get('target_class_names')
        print('收到target_class_names:', target_class_names)
        if not image_base64:
            return jsonify({"error": "请提供base64图片数据"}), 400
        if not model_name:
            return jsonify({"error": "请提供模型名称"}), 400
        if current_model != model_name:
            model_path = os.path.join(MODEL_FOLDER, model_name)
            if not os.path.exists(model_path):
                return jsonify({"error": f"模型文件不存在: {model_name}"}), 400
            if not detector.load_model(model_path):
                return jsonify({"error": "模型加载失败"}), 500
        result = detector.detect_image_base64(image_base64, conf_threshold, iou_threshold)
        if "error" in result:
            return jsonify(result), 500
        if target_class_names:
            filtered = [d for d in result["detections"] if d["class_name"] in target_class_names]
            print('过滤后detections:', filtered)
            result["detections"] = filtered
            result["total_detections"] = len(filtered)
        result["detection_info"] = {
            "model_name": model_name,
            "detection_time": datetime.now().isoformat(),
            "conf_threshold": conf_threshold,
            "iou_threshold": iou_threshold,
            "target_class_names": target_class_names
        }
        return jsonify(result)
    except Exception as e:
        logger.error(f"图片检测失败: {str(e)}")
        return jsonify({"error": f"图片检测失败: {str(e)}"}), 500


@app.route('/api/model_status')
def model_status():
    """获取当前模型状态"""
    return jsonify({
        "current_model": current_model,
        "model_info": model_info,
        "model_loaded": current_model is not None
    })


@app.route('/api/list_models')
def list_models():
    """获取可用模型列表"""
    model_files = []
    if os.path.exists(MODEL_FOLDER):
        for file in os.listdir(MODEL_FOLDER):
            if file.endswith('.pt'):
                model_files.append(file)
    return jsonify({"models": model_files})


@app.route('/api/list_images')
def list_images():
    """获取可用测试图片列表"""
    test_images = []
    if os.path.exists(TESTPIC_FOLDER):
        for file in os.listdir(TESTPIC_FOLDER):
            if file.lower().endswith(('.jpg', '.jpeg', '.png', '.bmp')):
                test_images.append(file)
    return jsonify({"images": test_images})


@app.route('/api/list_datasets', methods=['GET'])
def list_datasets():
    files = []
    for f in os.listdir(UPLOAD_FOLDER):
        if f.lower().endswith('.zip'):
            files.append(f)
    return jsonify({"datasets": files})


@app.route('/api/detect_video_base64', methods=['POST'])
def detect_video_base64():
    global current_model, model_info

    try:
        data = request.get_json()
        if not data:
            return jsonify({"error": "请提供JSON数据"}), 400

        model_name = data.get('model_name')
        video_base64 = data.get('video_base64')
        conf_threshold = data.get('conf_threshold', 0.25)
        iou_threshold = data.get('iou_threshold', 0.45)
        frame_interval = data.get('frame_interval', 1)

        if not model_name:
            return jsonify({"error": "请提供模型名称"}), 400

        if not video_base64:
            return jsonify({"error": "请提供视频base64数据"}), 400

        # 检查模型是否已加载
        if current_model != model_name:
            # 加载指定模型
            model_path = os.path.join(MODEL_FOLDER, model_name)
            if not os.path.exists(model_path):
                return jsonify({"error": f"模型文件不存在: {model_name}"}), 400

            if not detector.load_model(model_path):
                return jsonify({"error": f"模型加载失败: {model_name}"}), 500

            current_model = model_name
            model_info = {
                "name": model_name,
                "path": model_path,
                "load_time": datetime.now().isoformat(),
                "status": "loaded"
            }

        # 执行视频检测
        result = detector.detect_video_base64(video_base64, model_name, conf_threshold, iou_threshold, frame_interval)

        if "error" in result:
            return jsonify(result), 500

        return jsonify(result)

    except Exception as e:
        logger.error(f"视频检测API错误: {str(e)}")
        return jsonify({"error": f"视频检测失败: {str(e)}"}), 500


@app.route('/api/upload_model', methods=['POST'])
def upload_model():
    """上传PT模型文件"""
    try:
        # 检查是否有文件上传
        if 'model_file' not in request.files:
            return jsonify({"error": "请选择要上传的模型文件"}), 400

        file = request.files['model_file']

        # 检查文件名
        if file.filename == '':
            return jsonify({"error": "未选择文件"}), 400

        # 检查文件扩展名
        if not file.filename.lower().endswith('.pt'):
            return jsonify({"error": "只支持.pt格式的模型文件"}), 400

        # 检查文件大小 (限制为100MB)
        file.seek(0, 2)  # 移动到文件末尾
        file_size = file.tell()
        file.seek(0)  # 重置到文件开头

        if file_size > 100 * 1024 * 1024:  # 100MB
            return jsonify({"error": "文件大小不能超过100MB"}), 400

        # 保存文件到model目录
        filename = file.filename
        file_path = os.path.join(MODEL_FOLDER, filename)

        # 确保model目录存在
        os.makedirs(MODEL_FOLDER, exist_ok=True)

        # 保存文件
        file.save(file_path)

        # 验证文件是否保存成功
        if not os.path.exists(file_path):
            return jsonify({"error": "文件保存失败"}), 500

        # 获取文件信息
        file_size_mb = os.path.getsize(file_path) / (1024 * 1024)

        logger.info(f"模型文件上传成功: {filename}, 大小: {file_size_mb:.2f}MB")

        return jsonify({
            "success": True,
            "message": f"模型文件 {filename} 上传成功",
            "model_info": {
                "filename": filename,
                "size_mb": round(file_size_mb, 2),
                "path": file_path,
                "upload_time": datetime.now().isoformat()
            }
        })

    except Exception as e:
        logger.error(f"模型文件上传失败: {str(e)}")
        return jsonify({"error": f"模型文件上传失败: {str(e)}"}), 500


@app.route('/api/upload_dataset', methods=['POST'])
def upload_dataset():
    file = request.files.get('dataset_zip')
    if not file:
        return jsonify({"error": "请上传数据集zip文件"}), 400
    filename = file.filename
    save_path = os.path.join(UPLOAD_FOLDER, filename)
    file.save(save_path)
    return jsonify({"success": True, "filename": filename, "save_path": save_path})


@app.route('/api/train_yolo', methods=['POST'])
def train_yolo():
    """
    YOLO训练接口：指定zip文件名，训练模型，返回结果
    """
    try:
        # 1. 获取参数
        zip_filename = request.form.get('zip_filename')
        if not zip_filename:
            return jsonify({"error": "请提供zip文件名"}), 400
        zip_path = os.path.join(UPLOAD_FOLDER, zip_filename)
        if not os.path.exists(zip_path):
            return jsonify({"error": "zip文件不存在"}), 400
        exp_name = request.form.get('exp_name', f"exp_{int(time.time())}")
        epochs = int(request.form.get('epochs', 50))
        batch = int(request.form.get('batch_size', 16))
        img = int(request.form.get('img_size', 640))
        model_cfg = request.form.get('model_cfg', 'yolov5s.yaml')
        weights = request.form.get('pretrained_weights', 'yolov5s.pt')

        # 2. 解压数据集到 datasets/exp_name
        save_dir = os.path.join('datasets', exp_name)
        os.makedirs(save_dir, exist_ok=True)
        with zipfile.ZipFile(zip_path, 'r') as zip_ref:
            zip_ref.extractall(save_dir)
        data_yaml = os.path.join(save_dir, 'data.yaml')
        if not os.path.exists(data_yaml):
            return jsonify({"error": "data.yaml未找到，请检查数据集结构"}), 400

        import sys
        train_cmd = [
            sys.executable, 'train.py',
            '--img', str(img),
            '--batch', str(batch),
            '--epochs', str(epochs),
            '--data', os.path.join('datasets', exp_name, 'data.yaml'),
            '--cfg', model_cfg,
            '--weights', weights,
            '--name', exp_name
        ]
        result = subprocess.run(train_cmd, capture_output=True, text=True)
        log = result.stdout + "\n" + result.stderr

        # 4. 读取结果图片
        result_img_path = os.path.join('runs', 'train', exp_name, 'results.png')
        img_base64 = None
        if os.path.exists(result_img_path):
            with open(result_img_path, 'rb') as f:
                img_base64 = base64.b64encode(f.read()).decode()
        # 5. 读取主要指标
        metrics = {}
        results_txt = os.path.join('runs', 'train', exp_name, 'results.txt')
        if os.path.exists(results_txt):
            with open(results_txt, 'r', encoding='utf-8') as f:
                metrics['results'] = f.read()
        # 6. 返回
        return jsonify({
            "success": True,
            "exp_name": exp_name,
            "result_image_base64": img_base64,
            "train_log": log,
            "metrics": metrics,
            "model_download_url": f"/api/download_model?exp_name={exp_name}"
        })
    except Exception as e:
        logger.error(f"训练失败: {str(e)}")
        return jsonify({"error": f"训练失败: {str(e)}"}), 500


@app.route('/api/download_model')
def download_model():
    exp_name = request.args.get('exp_name')
    if not exp_name:
        return jsonify({"error": "缺少exp_name参数"}), 400
    model_path = os.path.join('runs', 'train', exp_name, 'weights', 'best.pt')
    if not os.path.exists(model_path):
        return jsonify({"error": "模型文件不存在"}), 404
    return send_file(model_path, as_attachment=True)


@app.route('/api/train_result_image')
def train_result_image():
    exp_name = request.args.get('exp_name')
    if not exp_name:
        return jsonify({"error": "缺少exp_name参数"}), 400
    img_path = os.path.join('runs', 'train', exp_name, 'results.png')
    if not os.path.exists(img_path):
        return jsonify({"error": "结果图片不存在"}), 404
    return send_file(img_path, mimetype='image/png')


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
