// 设备状态类型
export interface DeviceStatus {
  id: string;
  name: string;
  status: 'normal' | 'warning' | 'error' | 'offline';
  location: string;
  lastUpdate: string;
}

// 报警记录类型
export interface AlarmRecord {
  id: string;
  deviceId: string;
  deviceName: string;
  level: 'low' | 'medium' | 'high';
  type: 'temperature' | 'pressure' | 'vibration' | 'power';
  message: string;
  timestamp: string;
  status: 'unread' | 'read' | 'handled';
}

// 停机记录类型
export interface ShutdownRecord {
  id: string;
  deviceId: string;
  deviceName: string;
  startTime: string;
  endTime: string;
  duration: number;
  reason: string;
  operator: string;
}

// 系统日志类型
export interface SystemLog {
  id: string;
  type: 'info' | 'warning' | 'error';
  message: string;
  timestamp: string;
  operator: string;
}

// 用户信息类型
export interface UserInfo {
  id: string;
  username: string;
  role: 'admin' | 'operator' | 'viewer';
  lastLogin: string;
  status: 'active' | 'inactive';
}

// 设备位置类型
export interface DeviceLocation {
  id: string;
  name: string;
  longitude: number;
  latitude: number;
  status: DeviceStatus['status'];
}

// 统计数据类型
export interface Statistics {
  total: number;
  normal: number;
  warning: number;
  error: number;
  offline: number;
}

// API响应类型
export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

// 分页参数类型
export interface PaginationParams {
  page: number;
  pageSize: number;
  sortBy?: string;
  sortOrder?: 'asc' | 'desc';
  keyword?: string;
}

// 分页响应类型
export interface PaginatedResponse<T> {
  total: number;
  items: T[];
  page: number;
  pageSize: number;
}

export * from '../api/types'; 