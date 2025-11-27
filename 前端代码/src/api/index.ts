/**
 * @file api/index.ts
 * @description API接口统一管理文件
 * @author Fhx0902
 * @date 2025-04-19
 */

import axios, { AxiosInstance, AxiosRequestConfig } from 'axios';
import { useUserStore } from '@/stores/user';

// 基础响应类型定义
export interface ApiResponseType<T = any> {
  code: number;      // 响应状态码
  data: T;          // 响应数据
  info: string;     // 响应信息
}

// 分页请求参数接口
export interface PaginationParams {
  pageNo: number;    // 当前页码
  pageSize: number;  // 每页条数
}

// 分页响应数据接口
export interface PaginationResponse<T> {
  list: T[];         // 数据列表
  pageNo: number;    // 当前页码
  pageSize: number;  // 每页条数
  pageTotal: number; // 总页数
}

// 创建axios实例
const request: AxiosInstance = axios.create({
  baseURL: '/api',  // API基础路径
  timeout: 5000,    // 请求超时时间
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded'
  }
});

/**
 * HTTP请求工具类
 */
const http = {
  /**
   * 发送POST请求
   * @param url - 请求地址
   * @param data - 请求数据
   * @returns Promise<ApiResponseType>
   */
  async post<T = any>(url: string, data?: any): Promise<ApiResponseType<T>> {
    try {
      const userStore = useUserStore();
      const token = userStore.getToken();
      
      // 构建FormData数据
      const formData = new URLSearchParams();
      if (data) {
        Object.entries(data).forEach(([key, value]) => {
          if (value !== undefined && value !== null) {
            formData.append(key, value.toString());
          }
        });
      }

      const config: AxiosRequestConfig = {
        method: 'POST',
        url,
        data: formData,
        headers: token ? { 'token': token } : undefined
      };

      const response = await request(config);
      return response.data;
    } catch (error) {
      this.handleError(error);
      throw error;
    }
  },

  /**
   * 错误处理
   * @param error - 错误对象
   */
  handleError(error: any): void {
    if (error.response?.status === 401) {
      const userStore = useUserStore();
      userStore.clearUser();
      window.location.href = '/login';
    }
  }
};

// 工作台相关接口
export const workspaceApi = {
  /**
   * 获取工作台概览数据
   */
  async getOverview(): Promise<ApiResponseType> {
    return http.post('/workspace/overview');
  },
  
  /**
   * 获取设备状态统计
   */
  async getDeviceStats(): Promise<ApiResponseType> {
    return http.post('/workspace/deviceStats');
  },
  
  /**
   * 获取报警趋势数据
   */
  async getAlarmTrend(): Promise<ApiResponseType> {
    return http.post('/workspace/alarmTrend');
  },
  
  /**
   * 获取待处理报警列表
   * @param params - 分页参数和筛选条件
   */
  async getPendingAlarms(params: PaginationParams & {
    status?: string;
  }): Promise<ApiResponseType<PaginationResponse<any>>> {
    return http.post('/workspace/pendingAlarms', params);
  }
};

// 系统管理相关接口
export const systemApi = {
  /**
   * 获取用户列表
   * @param params - 查询参数
   */
  async getUserList(params: PaginationParams & {
    name?: string;
    roleType?: number;
    status?: number;
  }): Promise<ApiResponseType<PaginationResponse<any>>> {
    return http.post('/user/loadList', params);
  }
};

// 账号相关接口
export const accountApi = {
  /**
   * 用户登录
   * @param formData - 登录表单数据
   */
  async login(formData: { 
    phoneNumber: string; 
    password: string; 
  }): Promise<ApiResponseType> {
    return http.post('/account/login', formData);
  },

  /**
   * 用户登出
   * @param userId - 用户ID
   */
  async logout(userId: number): Promise<ApiResponseType> {
    return http.post('/account/logout', { userId });
  },
  
  /**
   * 用户注册
   * @param data - 注册信息
   */
  async register(data: { 
    username: string; 
    phoneNumber: string; 
    password: string; 
  }): Promise<ApiResponseType> {
    return http.post('/account/register', data);
  },

  /**
   * 修改密码
   * @param data - 密码修改信息
   */
  async changePassword(data: { 
    userId: number; 
    oldPassword: string; 
    newPassword: string; 
  }): Promise<ApiResponseType> {
    return http.post('/account/changePassword', data);
  }
};

// 设备相关接口
export const deviceApi = {
  /**
   * 获取设备列表
   */
  async getList(): Promise<ApiResponseType> {
    return http.post('/device/list');
  },
  
  /**
   * 获取设备状态统计
   */
  async countStatus(): Promise<ApiResponseType<{
    runningCount: number;    // 运行中设备数量
    alarmCount: number;      // 报警设备数量
    runningPercent: number;  // 运行中设备百分比
    alarmPercent: number;    // 报警设备百分比
  }>> {
    return http.post('/device/countStatus');
  },

  /**
   * 获取设备类型统计
   */
  async countType(): Promise<ApiResponseType> {
    return http.post('/device/countType');
  }
};

// 报警管理相关接口
export const alarmApi = {
  /**
   * 获取报警列表
   * @param params - 查询参数
   */
  async loadList(params: PaginationParams & {
    deviceType?: number;
    status?: number;
    startTime?: string;
    endTime?: string;
  }): Promise<ApiResponseType<PaginationResponse<any>>> {
    return http.post('/alarm/loadList', params);
  },
  
  /**
   * 处理报警
   * @param alarmId - 报警ID
   */
  async handleAlarm(alarmId: number): Promise<ApiResponseType> {
    return http.post('/alarm/changeToProcessed', { alarmId });
  },
  
  /**
   * 获取报警状态统计
   */
  async getStatusCount(): Promise<ApiResponseType<{
    processedCount: number;    // 已处理数量
    unprocessedCount: number;  // 未处理数量
  }>> {
    return http.post('/alarm/countStatus');
  },

  /**
   * 获取报警类型统计
   */
  async countType(): Promise<ApiResponseType> {
    return http.post('/alarm/countType');
  },

  /**
   * 获取月度报警时间统计
   */
  async countAlarmTimeByMonth(): Promise<ApiResponseType> {
    return http.post('/alarm/countAlarmTimeByMonth');
  },

  /**
   * 获取月度处理时间统计
   */
  async countProcessedTimeByMonth(): Promise<ApiResponseType> {
    return http.post('/alarm/countProcessedTimeByMonth');
  }
};

// 用户管理相关接口
export interface UserListItem {
  userId: number;
  name: string;
  phoneNumber: string;
  roleType: number;
  status: number;
}

export const userApi = {
  /**
   * 创建或更新用户
   * @param data - 用户信息
   */
  async update(data: {
    userId?: number;
    name: string;
    phoneNumber: string;
    roleType: number;
  }): Promise<ApiResponseType> {
    return http.post('/user/createOrUpdate', data);
  },

  /**
   * 删除用户
   * @param userId - 用户ID
   */
  async delete(userId: number): Promise<ApiResponseType> {
    return http.post('/user/delete', { userId });
  },

  /**
   * 获取用户列表
   * @param params - 查询参数
   */
  async loadList(params: PaginationParams & {
    name?: string;
    roleType?: number;
    status?: number;
  }): Promise<ApiResponseType<PaginationResponse<UserListItem>>> {
    return http.post('/user/loadList', params);
  }
};

// 测点相关接口
export interface MeasurementPointItem {
  id: number;
  deviceName: string;
  measuringPointName: string;
  measuringPointStatus: number;
  workshop: string;
  description?: string;
}

export const mpApi = {
  /**
   * 获取测点列表
   * @param params - 查询参数
   */
  async loadList(params: PaginationParams & {
    workshop?: string;
    deviceName?: string;
    measuringPointStatus?: number;
    measuringPointName?: string;
    workshopName?: string;
  }): Promise<ApiResponseType<PaginationResponse<MeasurementPointItem>>> {
    return http.post('/mp/loadList', params);
  },

  /**
   * 加载测点图像
   * @param params - 测点参数
   */
  async loadImage(params: {
    mpId: number;
  }): Promise<ApiResponseType> {
    return http.post('/mp/loadImage', params);
  },

  /**
   * 获取测点详情
   * @param params - 测点参数
   */
  async getDetail(params: {
    mpId: number;
  }): Promise<ApiResponseType> {
    return http.post('/mp/getDetail', params);
  }
};

// 车间相关接口
export const workshopApi = {
  /**
   * 获取设备数量统计
   */
  async countDevice(): Promise<ApiResponseType> {
    return http.post('/workshop/countDevice');
  }
};

// 检测相关接口
export const detectApi = {
  /**
   * 执行目标检测
   * @param fileData - 文件数据
   * @param modelName - 模型名称
   */
  async detect(fileData: FormData, modelName: string): Promise<ApiResponseType> {
    try {
      fileData.append('modelName', modelName);
      const userStore = useUserStore();
      const token = userStore.getToken();

      const response = await axios({
        method: 'POST',
        url: '/api/detect/detect',
        data: fileData,
        headers: {
          'Content-Type': 'multipart/form-data',
          'token': token
        }
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  }
};
