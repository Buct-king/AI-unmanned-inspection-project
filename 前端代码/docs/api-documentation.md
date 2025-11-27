# 前端API接口开发文档

## 1. 概述

本文档详细说明了前端项目中的API接口规范和使用方法。项目使用TypeScript开发，采用Axios进行HTTP请求，并实现了统一的请求处理和错误处理机制。

## 2. 基础配置

### 2.1 请求实例配置

```typescript
const request: AxiosInstance = axios.create({
  baseURL: '/api',  // API基础路径
  timeout: 5000,    // 请求超时时间
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded'
  }
});
```

### 2.2 通用响应类型

```typescript
interface ApiResponse<T = any> {
  code: number;      // 响应状态码
  data: T;          // 响应数据
  info: string;     // 响应信息
}
```

### 2.3 分页相关类型

```typescript
interface PaginationParams {
  pageNo: number;    // 当前页码
  pageSize: number;  // 每页条数
}

interface PaginationResponse<T> {
  list: T[];         // 数据列表
  pageNo: number;    // 当前页码
  pageSize: number;  // 每页条数
  pageTotal: number; // 总页数
}
```

## 3. API模块说明

### 3.1 工作台模块 (workspaceApi)

工作台模块提供系统概览和统计数据的接口。

#### 3.1.1 获取工作台概览数据
```typescript
getOverview(): Promise<ApiResponse>
```

#### 3.1.2 获取设备状态统计
```typescript
getDeviceStats(): Promise<ApiResponse>
```

#### 3.1.3 获取报警趋势数据
```typescript
getAlarmTrend(): Promise<ApiResponse>
```

#### 3.1.4 获取待处理报警列表
```typescript
getPendingAlarms(params: PaginationParams & {
  status?: string;
}): Promise<ApiResponse<PaginationResponse<any>>>
```

### 3.2 账号模块 (accountApi)

账号模块处理用户认证相关的接口。

#### 3.2.1 用户登录
```typescript
login(formData: {
  phoneNumber: string;
  password: string;
}): Promise<ApiResponse>
```

#### 3.2.2 用户登出
```typescript
logout(userId: number): Promise<ApiResponse>
```

#### 3.2.3 用户注册
```typescript
register(data: {
  username: string;
  phoneNumber: string;
  password: string;
}): Promise<ApiResponse>
```

#### 3.2.4 修改密码
```typescript
changePassword(data: {
  userId: number;
  oldPassword: string;
  newPassword: string;
}): Promise<ApiResponse>
```

### 3.3 设备模块 (deviceApi)

设备模块提供设备管理和监控相关的接口。

#### 3.3.1 获取设备列表
```typescript
getList(): Promise<ApiResponse>
```

#### 3.3.2 获取设备状态统计
```typescript
countStatus(): Promise<ApiResponse<{
  runningCount: number;    // 运行中设备数量
  alarmCount: number;      // 报警设备数量
  runningPercent: number;  // 运行中设备百分比
  alarmPercent: number;    // 报警设备百分比
}>>
```

#### 3.3.3 获取设备类型统计
```typescript
countType(): Promise<ApiResponse>
```

### 3.4 报警管理模块 (alarmApi)

报警管理模块处理系统报警相关的接口。

#### 3.4.1 获取报警列表
```typescript
loadList(params: PaginationParams & {
  deviceType?: number;
  status?: number;
  startTime?: string;
  endTime?: string;
}): Promise<ApiResponse<PaginationResponse<any>>>
```

#### 3.4.2 处理报警
```typescript
handleAlarm(alarmId: number): Promise<ApiResponse>
```

#### 3.4.3 获取报警状态统计
```typescript
getStatusCount(): Promise<ApiResponse<{
  processedCount: number;    // 已处理数量
  unprocessedCount: number;  // 未处理数量
}>>
```

#### 3.4.4 获取报警类型统计
```typescript
countType(): Promise<ApiResponse>
```

#### 3.4.5 获取月度报警时间统计
```typescript
countAlarmTimeByMonth(): Promise<ApiResponse>
```

#### 3.4.6 获取月度处理时间统计
```typescript
countProcessedTimeByMonth(): Promise<ApiResponse>
```

### 3.5 用户管理模块 (userApi)

用户管理模块提供用户CRUD操作的接口。

#### 3.5.1 用户列表项类型
```typescript
interface UserListItem {
  userId: number;
  name: string;
  phoneNumber: string;
  roleType: number;
  status: number;
}
```

#### 3.5.2 创建或更新用户
```typescript
update(data: {
  userId?: number;
  name: string;
  phoneNumber: string;
  roleType: number;
}): Promise<ApiResponse>
```

#### 3.5.3 删除用户
```typescript
delete(userId: number): Promise<ApiResponse>
```

#### 3.5.4 获取用户列表
```typescript
loadList(params: PaginationParams & {
  name?: string;
  roleType?: number;
  status?: number;
}): Promise<ApiResponse<PaginationResponse<UserListItem>>>
```

### 3.6 测点模块 (measurementPointApi)

测点模块处理设备测点相关的接口。

#### 3.6.1 测点列表项类型
```typescript
interface MeasurementPointItem {
  id: number;
  deviceName: string;
  measuringPointName: string;
  measuringPointStatus: number;
  workshop: string;
  description?: string;
}
```

#### 3.6.2 获取测点列表
```typescript
loadList(params: PaginationParams & {
  workshop?: string;
  deviceName?: string;
  measuringPointStatus?: number;
  measuringPointName?: string;
  workshopName?: string;
}): Promise<ApiResponse<PaginationResponse<MeasurementPointItem>>>
```

#### 3.6.3 加载测点图像
```typescript
loadImage(params: {
  mpId: number;
}): Promise<ApiResponse>
```

#### 3.6.4 获取测点详情
```typescript
getDetail(params: {
  mpId: number;
}): Promise<ApiResponse>
```

#### 3.6.5 获取设备数量统计
```typescript
countDevice(): Promise<ApiResponse>
```

#### 3.6.6 执行目标检测
```typescript
detect(fileData: FormData, modelName: string): Promise<ApiResponse>
```

## 4. 使用示例

### 4.1 登录示例
```typescript
import { accountApi } from '@/api';

async function handleLogin() {
  try {
    const response = await accountApi.login({
      phoneNumber: '13800138000',
      password: 'password123'
    });
    if (response.code === 200) {
      // 登录成功处理
    }
  } catch (error) {
    // 错误处理
  }
}
```

### 4.2 获取分页数据示例
```typescript
import { userApi } from '@/api';

async function loadUserList() {
  try {
    const response = await userApi.loadList({
      pageNo: 1,
      pageSize: 10,
      name: '张三',
      roleType: 1
    });
    if (response.code === 200) {
      const { list, pageTotal } = response.data;
      // 处理数据
    }
  } catch (error) {
    // 错误处理
  }
}
```

## 5. 错误处理

所有API请求都会自动处理以下情况：
1. 请求超时
2. 网络错误
3. 401未授权（自动跳转到登录页）
4. 服务器错误

## 6. 注意事项

1. 所有请求都会自动携带token（除了登录请求）
2. 分页接口统一使用`pageNo`和`pageSize`参数
3. 文件上传使用`FormData`格式
4. 所有接口返回数据都包含在`ApiResponse`类型中
5. 使用TypeScript类型提示可以更好地进行开发

## 7. 开发规范

1. 新增接口时需要在对应的模块下添加
2. 接口命名要语义化，使用动词+名词的形式
3. 所有接口都需要添加TypeScript类型定义
4. 分页接口统一使用`PaginationParams`和`PaginationResponse`类型
5. 接口文档要及时更新 