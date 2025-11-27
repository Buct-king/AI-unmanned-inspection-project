import axios from 'axios';
import { useUserStore } from '@/stores/user';

// Spring Boot API baseURL
const baseURL = (import.meta as any).env.VITE_API_BASE_URL || '/api';

const yoloRequest = axios.create({
  baseURL,
  timeout: 3000000
});

yoloRequest.interceptors.request.use(config => {
  const userStore = useUserStore();
  const token = userStore.getToken?.();
  if (token) {
    config.headers = config.headers || {};
    config.headers['token'] = token;
  }
  return config;
});

export const yoloApi = {
  // 健康检查
  async healthCheck(): Promise<any> {
    const res = await yoloRequest.get('/yolo/health');
    return res.data;
  },
  // 获取模型列表
  async getModelList(): Promise<string[]> {
    const userStore = useUserStore();
    const token = userStore.getToken?.();
    const res = await yoloRequest.get('/yolo/models', { headers: { token } });
    if (res.data.code !== 200) throw new Error(res.data.info || '获取模型列表失败');
    return res.data.data?.models || [];
  },
  // 获取模型状态
  async getModelStatus(): Promise<any> {
    const userStore = useUserStore();
    const token = userStore.getToken?.();
    const res = await yoloRequest.get('/yolo/model/status', { headers: { token } });
    if (res.data.code !== 200) throw new Error(res.data.info || '获取模型状态失败');
    return res.data.data;
  },
  // 加载模型
  async loadModel(modelName: string): Promise<any> {
    const userStore = useUserStore();
    const token = userStore.getToken?.();
    const res = await yoloRequest.post(`/yolo/model/load?modelName=${encodeURIComponent(modelName)}`, {}, { headers: { token } });
    if (res.data.code !== 200) throw new Error(res.data.info || '加载模型失败');
    return res.data.data;
  },
  // 图片检测
  async detectImage(modelName: string, imageBase64: string, params: { confThreshold?: number; iouThreshold?: number; target_class_names?: string[] }): Promise<any> {
    const userStore = useUserStore();
    const token = userStore.getToken?.();
    const body: any = {
      modelName,
      imageBase64,
      confThreshold: params.confThreshold,
      iouThreshold: params.iouThreshold,
      target_class_names: params.target_class_names // 确保传递类别
    };
    const res = await yoloRequest.post('/yolo/detect', body, { headers: { token, 'Content-Type': 'application/json' } });
    if (res.data.code !== 200) throw new Error(res.data.info || '图片检测失败');
    return res.data.data;
  },
  // 视频检测
  async detectVideo(modelName: string, videoBase64: string, params: { confThreshold?: number; iouThreshold?: number; target_class_names?: string[]; frameInterval?: number }): Promise<any> {
    const userStore = useUserStore();
    const token = userStore.getToken?.();
    const body: any = {
      modelName,
      videoBase64,
      confThreshold: params.confThreshold,
      iouThreshold: params.iouThreshold,
      target_class_names: params.target_class_names, // 确保传递类别
      frameInterval: params.frameInterval // 新增跳帧参数
    };
    const res = await yoloRequest.post('/yolo/detect/video', body, { headers: { token, 'Content-Type': 'application/json' } });
    if (res.data.code !== 200) throw new Error(res.data.info || '视频检测失败');
    return res.data.data;
  },
  // 上传模型文件，支持 title/description
  async uploadModel(file: File, meta?: { title?: string; description?: string }) {
    const userStore = useUserStore();
    const token = userStore.getToken?.();
    const formData = new FormData();
    formData.append('modelFile', file);
    if (meta?.title) formData.append('title', meta.title);
    if (meta?.description) formData.append('description', meta.description);
    const res = await yoloRequest.post('/yolo/upload/model', formData, { headers: { token, 'Content-Type': 'multipart/form-data' } });
    if (res.data.code !== 200) throw new Error(res.data.info || '上传模型失败');
    return res.data.data;
  },
  // 获取指定PT模型信息
  async getPtInfo(fileName: string) {
    const userStore = useUserStore();
    const token = userStore.getToken?.();
    const res = await yoloRequest.get(`/model_pt/${encodeURIComponent(fileName)}`, { headers: { token } });
    // 兼容无 code 字段的情况
    if (res.data.code !== undefined && res.data.code !== 200) throw new Error(res.data.info || '获取模型信息失败');
    return res.data.data || res.data;
  },
  // 工具：文件转base64
  fileToBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => {
        const result = reader.result as string;
        const base64 = result.split(',')[1];
        resolve(base64);
      };
      reader.onerror = reject;
      reader.readAsDataURL(file);
    });
  },
  // 获取鉴权头
  getAuthHeaders(): Record<string, string> {
    const userStore = useUserStore();
    const token = userStore.getToken?.();
    return token ? { token } : {};
  },
  // 获取摄像头流URL，支持camera_idx和frameInterval
  getCameraStreamUrl(model: string, camera_idx: string|number = 0, frameInterval?: number) {
    const userStore = useUserStore();
    const token = userStore.getToken?.();
    let url = `/api/camera/stream?model=${encodeURIComponent(model)}&camera_idx=${encodeURIComponent(camera_idx)}`;
    if (typeof frameInterval === 'number' && frameInterval > 1) {
      url += `&frameInterval=${frameInterval}`;
    }
    if (token) url += `&token=${encodeURIComponent(token)}`;
    return url;
  },
  // 启动检测，支持frameInterval
  async startCameraDetection(model: string, camera_idx: string|number = 0, frameInterval?: number) {
    const userStore = useUserStore();
    const token = userStore.getToken?.();
    const body: any = { camera_idx, model };
    if (typeof frameInterval === 'number' && frameInterval > 1) {
      body.frameInterval = frameInterval;
    }
    const res = await yoloRequest.post('/camera/start', body, { headers: { token } });
    if (res.data.code !== 200) throw new Error(res.data.info || '启动检测失败');
    return res.data.data;
  },
  // 停止检测
  async stopCameraDetection(camera_idx: string|number = 0) {
    const userStore = useUserStore();
    const token = userStore.getToken?.();
    const res = await yoloRequest.post('/camera/stop', { camera_idx }, { headers: { token } });
    if (res.data.code !== 200) throw new Error(res.data.info || '停止检测失败');
    return res.data.data;
  },
  // 释放摄像头流资源
  async stopCameraStream(camera_idx: string | number) {
    const userStore = useUserStore();
    const token = userStore.getToken?.();
    const res = await yoloRequest.post('/camera/stream/stop', { camera_idx }, { headers: { token } });
    if (res.data && res.data.code !== 200) throw new Error(res.data.info || '关闭摄像头流失败');
    return res.data;
  },
  // 高层封装：只看流，支持frameInterval
  async watchStream(model: string, camera_idx: string|number, frameInterval?: number) {
    // 只返回流URL
    return this.getCameraStreamUrl(model, camera_idx, frameInterval);
  },
  // 高层封装：退出只看流
  async exitWatchStream() {
    // 前端只需清空流即可，无需请求后端
    return '';
  },
  // 高层封装：开始录制，支持frameInterval
  async recordStream(model: string, camera_idx: string|number, frameInterval?: number) {
    await this.startCameraDetection(model, camera_idx, frameInterval);
    return this.getCameraStreamUrl(model, camera_idx, frameInterval);
  },
  // 高层封装：结束录制
  async stopRecordStream(camera_idx: string|number) {
    await this.stopCameraDetection(camera_idx);
    return '';
  },
  // 获取摄像头列表
  async getCameraList(): Promise<string[]> {
    const userStore = useUserStore();
    const token = userStore.getToken?.();
    const res = await yoloRequest.get('/camera/list', { headers: { token } });
    // 兼容直接数组或对象格式
    if (Array.isArray(res.data)) return res.data;
    if (res.data.code === 200 && Array.isArray(res.data.data)) return res.data.data;
    throw new Error(res.data.info || '获取摄像头列表失败');
  },
  // 获取原始视频列表
  async getRawVideoList(): Promise<string[]> {
    const res = await yoloRequest.get('/video/raw/list');
    return res.data;
  },
  // 获取检测后视频列表
  async getDetectVideoList(): Promise<string[]> {
    const res = await yoloRequest.get('/video/detect/list');
    return res.data;
  },
  // 获取原始视频播放地址
  getRawVideoUrl(filename: string) {
    const userStore = useUserStore();
    const token = userStore.getToken?.();
    let url = `${baseURL}/video/raw/${encodeURIComponent(filename)}`;
    if (token) url += `?token=${encodeURIComponent(token)}`;
    return url;
  },
  // 获取检测后视频播放地址
  getDetectVideoUrl(filename: string) {
    const userStore = useUserStore();
    const token = userStore.getToken?.();
    let url = `${baseURL}/video/detect/${encodeURIComponent(filename)}`;
    if (token) url += `?token=${encodeURIComponent(token)}`;
    return url;
  },
  // 获取所有摄像头与模型绑定列表
  async getCameraModelBindList() {
    const userStore = useUserStore();
    const token = userStore.getToken?.();
    const res = await yoloRequest.get('/camera_model_bind/list', {
      headers: { token }
    });
    return res.data;
  },
  // 新增摄像头与模型绑定
  async addCameraModelBind(data: { cameraId: string; cameraName: string; cameraSource: string; modelName: string; }) {
    const userStore = useUserStore();
    const token = userStore.getToken?.();
    const res = await yoloRequest.post('/camera_model_bind/add', data, {
      headers: { token }
    });
    return res.data;
  },
  // 修改摄像头与模型绑定
  async updateCameraModelBind(data: { id: number; cameraId: string; cameraName: string; cameraSource: string; modelName: string; }) {
    const userStore = useUserStore();
    const token = userStore.getToken?.();
    const res = await yoloRequest.put('/camera_model_bind/update', data, {
      headers: { token }
    });
    return res.data;
  },
  // 删除摄像头与模型绑定
  async deleteCameraModelBind(id: number) {
    const userStore = useUserStore();
    const token = userStore.getToken?.();
    const res = await yoloRequest.delete(`/camera_model_bind/delete/${id}`, {
      headers: { token }
    });
    return res.data;
  },
  // 获取单个摄像头与模型绑定详情
  async getCameraModelBindDetail(id: number) {
    const userStore = useUserStore();
    const token = userStore.getToken?.();
    const res = await yoloRequest.get(`/camera_model_bind/${id}`, {
      headers: { token }
    });
    return res.data;
  },
  // 发起检测（调用摄像头绑定的模型）
  async detectCameraModelBind(id: number) {
    const userStore = useUserStore();
    const token = userStore.getToken?.();
    const res = await yoloRequest.post(`/camera_model_bind/detect/${id}`, {}, {
      headers: { token }
    });
    return res.data;
  },
  // 自动检测海康摄像头RTSP流（ONVIF）
  async autoDetectRtsp(data: { ip: string; user: string; pwd: string; port?: number }) {
    const userStore = useUserStore();
    const token = userStore.getToken?.();
    const res = await yoloRequest.post('/camera/auto_detect_rtsp', data, {
      headers: { token }
    });
    return res.data;
  }
};

export type DetectionResult = {
  success: boolean;
  detections: Array<{
    className: string;
    confidence: number;
    bbox: number[];
  }>;
  annotatedImageBase64?: string;
  totalDetections?: number;
};

export type VideoDetectionResult = {
  processedVideoBase64?: string;
  totalFrames?: number;
  totalDetections?: number;
  videoInfo?: any;
  [key: string]: any;
};

export type YoloDetectionAPI = typeof yoloApi; 