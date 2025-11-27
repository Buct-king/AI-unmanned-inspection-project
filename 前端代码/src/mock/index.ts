import {
  DeviceStatus,
  AlarmRecord,
  ShutdownRecord,
  SystemLog,
  UserInfo,
  DeviceLocation,
  Statistics
} from '@/types';

// 生成随机ID
const generateId = (): string => {
  return Math.random().toString(36).substr(2, 9);
};

// 生成随机时间戳
const generateTimestamp = (): string => {
  const now = new Date();
  const randomDays = Math.floor(Math.random() * 30);
  const randomHours = Math.floor(Math.random() * 24);
  const randomMinutes = Math.floor(Math.random() * 60);
  now.setDate(now.getDate() - randomDays);
  now.setHours(now.getHours() - randomHours);
  now.setMinutes(now.getMinutes() - randomMinutes);
  return now.toISOString();
};

// 设备状态数据
export const deviceStatusData: DeviceStatus[] = Array.from({ length: 100 }, () => ({
  id: generateId(),
  name: `设备-${Math.floor(Math.random() * 1000)}`,
  status: ['normal', 'warning', 'error', 'offline'][Math.floor(Math.random() * 4)] as DeviceStatus['status'],
  location: `位置-${Math.floor(Math.random() * 10)}`,
  lastUpdate: generateTimestamp()
}));

// 报警记录数据
export const alarmRecordData: AlarmRecord[] = Array.from({ length: 100 }, () => ({
  id: generateId(),
  deviceId: deviceStatusData[Math.floor(Math.random() * deviceStatusData.length)].id,
  deviceName: deviceStatusData[Math.floor(Math.random() * deviceStatusData.length)].name,
  level: ['low', 'medium', 'high'][Math.floor(Math.random() * 3)] as AlarmRecord['level'],
  type: ['temperature', 'pressure', 'vibration', 'power'][Math.floor(Math.random() * 4)] as AlarmRecord['type'],
  message: `报警信息-${Math.floor(Math.random() * 1000)}`,
  timestamp: generateTimestamp(),
  status: ['unread', 'read', 'handled'][Math.floor(Math.random() * 3)] as AlarmRecord['status']
}));

// 停机记录数据
export const shutdownRecordData: ShutdownRecord[] = Array.from({ length: 100 }, () => {
  const startTime = generateTimestamp();
  const endTime = new Date(startTime);
  endTime.setHours(endTime.getHours() + Math.floor(Math.random() * 24));
  return {
    id: generateId(),
    deviceId: deviceStatusData[Math.floor(Math.random() * deviceStatusData.length)].id,
    deviceName: deviceStatusData[Math.floor(Math.random() * deviceStatusData.length)].name,
    startTime,
    endTime: endTime.toISOString(),
    duration: Math.floor(Math.random() * 24 * 60 * 60),
    reason: `停机原因-${Math.floor(Math.random() * 1000)}`,
    operator: `操作员-${Math.floor(Math.random() * 10)}`
  };
});

// 系统日志数据
export const systemLogData: SystemLog[] = Array.from({ length: 100 }, () => ({
  id: generateId(),
  type: ['info', 'warning', 'error'][Math.floor(Math.random() * 3)] as SystemLog['type'],
  message: `系统日志-${Math.floor(Math.random() * 1000)}`,
  timestamp: generateTimestamp(),
  operator: `操作员-${Math.floor(Math.random() * 10)}`
}));

// 用户信息数据
export const userInfoData: UserInfo[] = Array.from({ length: 100 }, () => ({
  id: generateId(),
  username: `用户-${Math.floor(Math.random() * 1000)}`,
  role: ['admin', 'operator', 'viewer'][Math.floor(Math.random() * 3)] as UserInfo['role'],
  lastLogin: generateTimestamp(),
  status: ['active', 'inactive'][Math.floor(Math.random() * 2)] as UserInfo['status']
}));

// 设备位置数据
export const deviceLocationData: DeviceLocation[] = Array.from({ length: 100 }, () => ({
  id: generateId(),
  name: `设备-${Math.floor(Math.random() * 1000)}`,
  longitude: 73 + Math.random() * 50,
  latitude: 3 + Math.random() * 50,
  status: ['normal', 'warning', 'error', 'offline'][Math.floor(Math.random() * 4)] as DeviceLocation['status']
}));

// 统计数据
export const statisticsData: Statistics = {
  total: deviceStatusData.length,
  normal: deviceStatusData.filter(d => d.status === 'normal').length,
  warning: deviceStatusData.filter(d => d.status === 'warning').length,
  error: deviceStatusData.filter(d => d.status === 'error').length,
  offline: deviceStatusData.filter(d => d.status === 'offline').length
}; 