# 前端项目开发文档

## 1. 项目概述

### 1.1 项目简介
本项目是一个基于Vue 3 + TypeScript的工业设备监控系统前端项目，用于实现设备状态监控、报警管理、用户管理等功能。

### 1.2 技术栈
- 核心框架：Vue 3
- 开发语言：TypeScript
- 构建工具：Vite
- 状态管理：Pinia
- UI框架：Element Plus
- HTTP客户端：Axios
- 路由管理：Vue Router
- 代码规范：ESLint + Prettier

### 1.3 项目结构
```
src/
├── api/          # API接口定义
├── assets/       # 静态资源
├── components/   # 公共组件
├── layouts/      # 布局组件
├── router/       # 路由配置
├── stores/       # 状态管理
├── styles/       # 全局样式
├── types/        # TypeScript类型定义
├── utils/        # 工具函数
├── views/        # 页面组件
├── App.vue       # 根组件
└── main.ts       # 入口文件
```

## 2. 开发环境搭建

### 2.1 环境要求
- Node.js >= 16.0.0
- npm >= 7.0.0

### 2.2 安装依赖
```bash
npm install
```

### 2.3 开发服务器
```bash
npm run dev
```

### 2.4 构建生产版本
```bash
npm run build
```

## 3. 项目规范

### 3.1 代码规范
- 使用ESLint进行代码检查
- 使用Prettier进行代码格式化
- 遵循TypeScript严格模式
- 组件名使用PascalCase
- 文件名使用kebab-case

### 3.2 目录规范
- 组件文件放在`components`目录
- 页面文件放在`views`目录
- 工具函数放在`utils`目录
- 类型定义放在`types`目录
- API接口定义放在`api`目录

### 3.3 命名规范
- 组件名：PascalCase
- 文件名：kebab-case
- 变量名：camelCase
- 常量名：UPPER_SNAKE_CASE
- CSS类名：kebab-case

## 4. 功能模块说明

### 4.1 用户认证模块
- 登录/注册
- 密码修改
- 用户信息管理
- Token管理

### 4.2 工作台模块
- 系统概览
- 设备状态统计
- 报警趋势分析
- 待处理报警列表

### 4.3 设备管理模块
- 设备列表
- 设备状态监控
- 设备类型统计
- 设备详情查看

### 4.4 报警管理模块
- 报警列表
- 报警处理
- 报警统计
- 报警趋势分析

### 4.5 测点管理模块
- 测点列表
- 测点状态监控
- 测点图像查看
- 目标检测功能

## 5. 组件开发规范

### 5.1 组件结构
```vue
<template>
  <!-- 模板内容 -->
</template>

<script lang="ts" setup>
// 组件逻辑
</script>

<style scoped>
/* 组件样式 */
</style>
```

### 5.2 Props定义
```typescript
interface Props {
  prop1: string;
  prop2?: number;
}

const props = defineProps<Props>();
```

### 5.3 事件处理
```typescript
const emit = defineEmits<{
  (e: 'update', value: string): void;
  (e: 'delete'): void;
}>();
```

## 6. 状态管理

### 6.1 Store结构
```typescript
// stores/user.ts
import { defineStore } from 'pinia';

export const useUserStore = defineStore('user', {
  state: () => ({
    // 状态定义
  }),
  actions: {
    // 操作方法
  },
  getters: {
    // 计算属性
  }
});
```

### 6.2 状态使用
```typescript
import { useUserStore } from '@/stores/user';

const userStore = useUserStore();
```

## 7. 路由管理

### 7.1 路由配置
```typescript
// router/index.ts
import { createRouter, createWebHistory } from 'vue-router';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    // 路由配置
  ]
});
```

### 7.2 路由守卫
```typescript
router.beforeEach((to, from, next) => {
  // 路由守卫逻辑
});
```

## 8. API接口规范

### 8.1 请求封装
```typescript
// api/index.ts
const http = {
  async post<T = any>(url: string, data?: any): Promise<ApiResponse<T>> {
    // 请求处理逻辑
  }
};
```

### 8.2 响应处理
```typescript
interface ApiResponse<T = any> {
  code: number;
  data: T;
  info: string;
}
```

## 9. 错误处理

### 9.1 全局错误处理
```typescript
app.config.errorHandler = (err, vm, info) => {
  // 错误处理逻辑
};
```

### 9.2 API错误处理
```typescript
try {
  const response = await api.someMethod();
} catch (error) {
  // 错误处理逻辑
}
```

## 10. 性能优化

### 10.1 代码分割
- 路由懒加载
- 组件异步加载
- 第三方库按需引入

### 10.2 缓存策略
- 合理使用keep-alive
- 数据缓存处理
- 图片懒加载

### 10.3 构建优化
- 压缩代码
- 提取公共代码
- 优化资源加载

## 11. 部署说明

### 11.1 构建
```bash
npm run build
```

### 11.2 部署步骤
1. 构建生产版本
2. 将dist目录下的文件部署到服务器
3. 配置nginx等web服务器
4. 配置HTTPS证书（如需要）

### 11.3 环境变量
```env
VITE_API_BASE_URL=xxx
VITE_APP_TITLE=xxx
```

## 12. 开发流程

### 12.1 功能开发流程
1. 需求分析
2. 技术方案设计
3. 代码实现
4. 代码审查
5. 测试验证
6. 部署上线

### 12.2 代码提交流程
1. 创建功能分支
2. 开发功能
3. 提交代码
4. 创建合并请求
5. 代码审查
6. 合并到主分支

## 13. 测试规范

### 13.1 单元测试
- 使用Vitest进行单元测试
- 测试覆盖率要求
- 测试用例编写规范

### 13.2 端到端测试
- 使用Cypress进行E2E测试
- 测试场景设计
- 测试报告生成

## 14. 文档维护

### 14.1 文档类型
- 开发文档
- API文档
- 部署文档
- 用户手册

### 14.2 文档更新
- 及时更新文档
- 保持文档同步
- 版本控制

## 15. 常见问题

### 15.1 开发问题
- 环境配置问题
- 依赖安装问题
- 构建问题

### 15.2 部署问题
- 服务器配置
- 环境变量配置
- 性能优化

## 16. 更新日志

### 16.1 版本记录
- v1.0.0 (2025-04-19)
  - 初始版本发布
  - 基础功能实现

### 16.2 更新计划
- 功能优化
- 性能提升
- 新特性开发 