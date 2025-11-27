/**
 * YOLO检测API工具类
 * 统一管理YOLO检测相关的API调用
 */

export interface ModelStatus {
  currentModel: string;
  modelLoaded: boolean;
  modelInfo?: {
    name: string;
    path: string;
    status: string;
    load_time: string;
    class_names?: string[];
  };
}

export interface DetectionParams {
  confThreshold: number;
  iouThreshold: number;
}

export interface DetectionResult {
  success: boolean;
  totalDetections: number;
  detections: Array<{
    className: string;
    confidence: number;
    bbox: number[];
  }>;
  annotatedImageBase64?: string;
}

export interface ApiResponse<T = any> {
  code: number;
  info: string;
  data: T;
}

class YoloDetectionAPI {
  private readonly API_BASE_URL = 'http://localhost:8080/api';
  private readonly FLASK_BASE_URL = 'http://localhost:5000';
  private readonly AUTH_TOKEN = 'c4ca4238a0b923820dcc509a6f75849b';

  /**
   * 获取认证头（对外公开）
   */
  public getAuthHeaders(): Record<string, string> {
    return {
      'token': this.AUTH_TOKEN,
      'Content-Type': 'application/json'
    };
  }

  /**
   * 健康检查
   */
  async healthCheck(): Promise<boolean> {
    try {
      const response = await fetch(`${this.API_BASE_URL}/yolo/health`, {
        method: 'GET',
        headers: this.getAuthHeaders()
      });
      const data: ApiResponse = await response.json();
      
      if (data.code === 200) {
        return true;
      } else {
        throw new Error(data.info);
      }
    } catch (error) {
      console.error('健康检查失败:', error);
      throw error;
    }
  }

  /**
   * 获取模型列表
   */
  async getModelList(): Promise<string[]> {
    try {
      const response = await fetch(`${this.API_BASE_URL}/yolo/models`, {
        method: 'GET',
        headers: this.getAuthHeaders()
      });
      const data: ApiResponse<{ models: string[] }> = await response.json();
      
      if (data.code === 200) {
        return data.data.models;
      } else {
        throw new Error(data.info);
      }
    } catch (error) {
      console.error('获取模型列表失败:', error);
      throw error;
    }
  }

  /**
   * 获取模型状态
   */
  async getModelStatus(): Promise<ModelStatus> {
    try {
      const response = await fetch(`${this.API_BASE_URL}/yolo/model/status`, {
        method: 'GET',
        headers: this.getAuthHeaders()
      });
      const data: ApiResponse<ModelStatus> = await response.json();
      
      if (data.code === 200) {
        return data.data;
      } else {
        throw new Error(data.info);
      }
    } catch (error) {
      console.error('获取模型状态失败:', error);
      throw error;
    }
  }

  /**
   * 加载模型
   */
  async loadModel(modelName: string): Promise<{ success: boolean; message: string; model_info?: any }> {
    try {
      const response = await fetch(`${this.API_BASE_URL}/yolo/model/load?modelName=${modelName}`, {
        method: 'POST',
        headers: this.getAuthHeaders()
      });
      const data: ApiResponse<{ success: boolean; message: string; model_info?: any }> = await response.json();
      
      if (data.code === 200) {
        return data.data;
      } else {
        throw new Error(data.info);
      }
    } catch (error) {
      console.error('加载模型失败:', error);
      throw error;
    }
  }

  /**
   * 图片检测
   */
  async detectImage(
    modelName: string,
    imageBase64: string,
    params: DetectionParams & Record<string, any>
  ): Promise<DetectionResult> {
    try {
      const body = {
        modelName,
        imageBase64,
        ...params
      };
      const response = await fetch(`${this.API_BASE_URL}/yolo/detect`, {
        method: 'POST',
        headers: this.getAuthHeaders(),
        body: JSON.stringify(body)
      });
      const data: ApiResponse<DetectionResult> = await response.json();
      if (data.code === 200) {
        return data.data;
      } else {
        throw new Error(data.info);
      }
    } catch (error) {
      console.error('图片检测失败:', error);
      throw error;
    }
  }

  /**
   * 文件转base64
   */
  fileToBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => {
        const base64 = reader.result as string;
        // 移除data:image/jpeg;base64,前缀
        const base64Data = base64.split(',')[1];
        resolve(base64Data);
      };
      reader.onerror = error => reject(error);
    });
  }

  /**
   * 上传模型文件
   */
  async uploadModel(modelFile: File): Promise<{ success: boolean; message: string }> {
    try {
      const formData = new FormData();
      formData.append('modelFile', modelFile);
      
      const response = await fetch(`${this.API_BASE_URL}/yolo/upload/model`, {
        method: 'POST',
        headers: { 'token': this.AUTH_TOKEN },
        body: formData
      });
      const data: ApiResponse<{ success: boolean; message: string }> = await response.json();
      
      if (data.code === 200) {
        return data.data;
      } else {
        throw new Error(data.info);
      }
    } catch (error) {
      console.error('上传模型失败:', error);
      throw error;
    }
  }

  /**
   * Flask API - 获取模型状态
   */
  async getFlaskModelStatus(): Promise<any> {
    try {
      const response = await fetch(`${this.FLASK_BASE_URL}/api/model_status`);
      const data = await response.json();
      return data;
    } catch (error) {
      console.error('获取Flask模型状态失败:', error);
      throw error;
    }
  }

  /**
   * Flask API - 获取模型列表
   */
  async getFlaskModelList(): Promise<string[]> {
    try {
      const response = await fetch(`${this.FLASK_BASE_URL}/api/list_models`);
      const data = await response.json();
      return data.models || [];
    } catch (error) {
      console.error('获取Flask模型列表失败:', error);
      throw error;
    }
  }

  /**
   * Flask API - 图片检测
   */
  async flaskDetectImage(
    modelName: string,
    imageBase64: string,
    params: DetectionParams
  ): Promise<DetectionResult> {
    try {
      const response = await fetch(`${this.FLASK_BASE_URL}/api/detect_base64`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          model_name: modelName,
          image_base64: imageBase64,
          conf_threshold: params.confThreshold,
          iou_threshold: params.iouThreshold
        })
      });
      const data = await response.json();
      
      if (data.success) {
        return {
          success: data.success,
          totalDetections: data.total_detections,
          detections: data.detections.map((d: any) => ({
            className: d.class_name,
            confidence: d.confidence,
            bbox: d.bbox
          })),
          annotatedImageBase64: data.annotated_image_base64
        };
      } else {
        throw new Error(data.message || '检测失败');
      }
    } catch (error) {
      console.error('Flask图片检测失败:', error);
      throw error;
    }
  }
}

// 创建单例实例
export const yoloApi = new YoloDetectionAPI();

// 导出类型（已在文件顶部导出，无需重复导出） 