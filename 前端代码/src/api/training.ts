/*
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-07-08 17:17:01
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-07-13 23:07:11
 * @FilePath: \front_flask\src\api\training.ts
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
import axios from 'axios';
import { useUserStore } from '@/stores/user';

const baseURL = (import.meta as any).env.VITE_API_BASE_URL || '/api';

const trainingRequest = axios.create({
  baseURL,
  timeout: 1800000 // 30分钟
});

// 自动加 token 拦截器
trainingRequest.interceptors.request.use(config => {
  const userStore = useUserStore();
  const token = userStore.getToken?.();
  if (token) {
    config.headers = config.headers || {};
    config.headers['token'] = token;
  }
  return config;
});

export const trainingApi = {
  // 上传数据集 zip
  async uploadDataset(file: File): Promise<any> {
    const userStore = useUserStore();
    const token = userStore.getToken?.();
    const formData = new FormData();
    formData.append('dataset_zip', file);
    const res = await trainingRequest.post('/upload_dataset', formData, {
      headers: { token, 'Content-Type': 'multipart/form-data' }
    });
    if (res.data.code && res.data.code !== 200) throw new Error(res.data.info || '上传数据集失败');
    return res.data;
  },
  // 获取 zip 文件列表
  async listDatasets(): Promise<string[]> {
    const userStore = useUserStore();
    const token = userStore.getToken?.();
    const res = await trainingRequest.get('/list_datasets', { headers: { token } });
    if (res.data.code && res.data.code !== 200) throw new Error(res.data.info || '获取数据集列表失败');
    return res.data.datasets || [];
  },
  // 发起训练
  async trainYolo(params: {
    zip_filename: string;
    exp_name?: string;
    epochs?: number;
    batch_size?: number;
    img_size?: number;
    model_cfg?: string;
    pretrained_weights?: string;
  }): Promise<any> {
    const userStore = useUserStore();
    const token = userStore.getToken?.();
    const formData = new FormData();
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        formData.append(key, value.toString());
      }
    });
    const res = await trainingRequest.post('/train_yolo', formData, { headers: { token } });
    if (res.data.code && res.data.code !== 200) throw new Error(res.data.info || '训练失败');
    return res.data;
  },
  // 下载训练好的模型（返回文件流，前端可直接跳转下载）
  getModelDownloadUrl(exp_name: string): string {
    return `/download_model?exp_name=${encodeURIComponent(exp_name)}`;
  },
  // 下载模型（带token，返回blob，前端触发下载）
  async downloadModel(exp_name: string): Promise<Blob> {
    const userStore = useUserStore();
    const token = userStore.getToken?.();
    const res = await axios.get(
      `/download_model?exp_name=${encodeURIComponent(exp_name)}`,
      {
        responseType: 'blob',
        headers: token ? { token } : {},
      }
    );
    return res.data;
  },
  // 获取训练结果图片（返回图片流，前端可直接用 <img :src="url" />）
  getTrainResultImageUrl(exp_name: string): string {
    return `/train_result_image?exp_name=${encodeURIComponent(exp_name)}`;
  }
}; 