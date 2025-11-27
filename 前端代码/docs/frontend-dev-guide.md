# 无人巡检系统前端开发文档

## 目录

1. [项目简介](#项目简介)
2. [技术栈与依赖](#技术栈与依赖)
3. [环境配置与部署](#环境配置与部署)
4. [目录结构说明](#目录结构说明)
5. [主要页面与功能](#主要页面与功能)
6. [通用组件说明](#通用组件说明)
7. [API 接口与参数](#api-接口与参数)
8. [样式与响应式布局](#样式与响应式布局)
9. [Token 鉴权与接口兼容](#token-鉴权与接口兼容)
10. [常见问题与对接注意事项](#常见问题与对接注意事项)
11. [调试与排查技巧](#调试与排查技巧)
12. [附录：接口文档与返回结构](#附录接口文档与返回结构)

---

## 项目简介

本项目为「无人巡检系统」前端，基于 Vue 3 + Element Plus + Pinia + ECharts + Axios，支持响应式自适应布局、暗黑模式、token 鉴权、视频流、模型训练与检测等复杂业务场景。前后端分离，接口对接 Spring Boot/Flask 后端。

---

## 技术栈与依赖

- **Vue 3**：主流渐进式前端框架
- **Element Plus**：UI 组件库，支持暗黑模式
- **Pinia**：状态管理
- **ECharts**：数据可视化
- **Axios**：HTTP 请求库
- **Sass/SCSS**：样式预处理
- **Vite**：前端构建工具
- **TypeScript**：类型支持（可选）

---

## 环境配置与部署

### 1. 安装依赖

```bash
npm install
# 或
yarn install
```

### 2. 本地开发

```bash
npm run dev
# 或
yarn dev
```
默认端口：5173，可在 `vite.config.js` 配置。

### 3. 打包构建

```bash
npm run build
# 或
yarn build
```
打包输出在 `dist/` 目录。

### 4. 生产部署

将 `dist/` 目录内容部署至 Nginx/Apache 或其他静态服务器，确保接口代理配置正确。

---

## 目录结构说明

```text
src/
  api/                # 所有后端接口封装
    index.ts          # 通用/基础API
    yolo.ts           # 检测相关API
    training.ts       # 训练相关API
  assets/             # 静态资源
  components/         # 通用组件
  layouts/            # 布局相关
  router/             # 路由配置
  stores/             # Pinia 状态管理
  styles/             # 全局与主题样式
  types/              # 类型定义
  utils/              # 工具函数
  views/              # 页面与业务模块
    features/         # 功能页（检测、训练、视频等）
  main.ts             # 入口文件
  App.vue             # 根组件
```

---

## 主要页面与功能

### 1. 首页 Home.vue

- 展示系统总览、设备状态、告警统计、快捷入口等
- 响应式三栏布局，适配 PC/平板/手机
- 卡片、图表自适应宽高

### 2. 登录 Login.vue

- 账号密码登录，支持 token 存储
- 登录后跳转首页

### 3. 工作台 Workspace.vue

- 个人工作区，展示待办、快捷操作等

### 4. 设备监控 DeviceMonitor.vue

- 实时设备状态、视频流展示
- 支持摄像头切换、流释放、状态追踪

### 5. 检测演示 EnhancedDetection.vue

- PT 模型上传、模型信息获取
- 检测参数设置、图片上传、检测结果展示
- 支持 title/description 元信息传递与渲染
- 响应式布局，图片、参数区自适应

### 6. 智能训练 SmartTraining.vue

- 训练数据上传、参数设置、训练进度展示
- 训练结果可视化

### 7. 视频库 VideoGallery.vue

- 视频流、历史录像展示
- 支持筛选、分页、响应式布局

### 8. 其它页面

- 告警管理 AlarmManagement.vue
- 系统管理 SystemManagement.vue
- 个人中心 Profile.vue
- 状态监控 StatusMonitoring.vue
- 设备详情 DeviceDetail.vue
- 告警处理 AlarmProcess.vue
- 404 NotFound.vue

---

## 通用组件说明

- **CommonHeader.vue**：顶部导航栏
- **CommonSidebar.vue**：侧边栏菜单
- **CommonBreadcrumb.vue**：面包屑导航
- **DeviceStatusCard.vue**：设备状态卡片
- **ThemeSwitch.vue**：暗黑/明亮模式切换
- **ReusableChart.vue**：通用图表封装

---

## API 接口与参数

### 1. API 分层

- `src/api/index.ts`：通用/基础业务接口（如登录、用户、设备、告警等）
- `src/api/yolo.ts`：检测相关接口（模型上传、检测、模型信息等）
- `src/api/training.ts`：训练相关接口

### 2. 典型接口示例

#### 2.1 检测相关（yoloApi）

- **上传 PT 模型**
  ```ts
  uploadPtModel(formData: FormData): Promise<any>
  // formData: { file, title, description }
  ```
- **获取模型信息**
  ```ts
  getPtInfo(): Promise<{ title: string, description: string }>
  ```
- **图片检测**
  ```ts
  detectImage(params: { image: File, modelId: string, ... }): Promise<any>
  ```

#### 2.2 训练相关（trainingApi）

- **上传训练数据**
- **开始训练**
- **获取训练进度**

#### 2.3 通用业务（indexApi）

- **登录**
- **获取用户信息**
- **设备列表/详情**
- **告警列表/处理**

### 3. 接口返回结构

- 标准结构：
  ```json
  {
    "code": 0,
    "msg": "success",
    "data": { ... }
  }
  ```
- 兼容结构（部分接口无 code 字段）：
  ```json
  {
    "title": "xxx",
    "description": "yyy"
  }
  ```
- 前端已做兼容处理，确保 title/description 能正常渲染。

### 4. Token 传递

- 登录成功后，token 存储于 Pinia/user.ts
- 后续请求通过 Axios 拦截器自动携带 token

---

## 样式与响应式布局

### 1. 全局样式

- `src/styles/base.scss`：全局基础样式
- `src/styles/element/dark.scss`：暗黑主题样式

### 2. 响应式布局

- 全部页面采用 el-row/el-col 栅格系统
- 卡片、内容区、图片、图表等用 flex、百分比、vw、max-width: 100% 等弹性单位
- 媒体查询 (max-width: 900px) 下三栏自动堆叠为一栏
- 卡片高度自适应内容，彻底去除横向滚动条
- 上传区、参数区、图片区、表格区等 gap、padding、border-radius 适配大屏/小屏
- 彻底去除写死 px，全部用弹性单位

### 3. 典型样式片段

```scss
.el-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}
.el-col {
  min-width: 240px;
  flex: 1 1 0;
}
@media (max-width: 900px) {
  .el-row {
    flex-direction: column;
  }
  .el-col {
    min-width: unset;
    width: 100%;
    flex: unset;
  }
}
.card {
  max-width: 100%;
  height: auto;
  box-sizing: border-box;
}
```

---

## Token 鉴权与接口兼容

- 登录后 token 存储于 Pinia，Axios 拦截器自动注入
- 部分接口返回结构不统一，前端已做兼容处理
- 图片/视频流采用 base64 或 url，注意跨域与格式兼容
- 流释放、按钮状态联动、调试输出等已做细致处理

---

## 常见问题与对接注意事项

1. **token 传递**：务必保证登录后所有接口都带 token
2. **接口兼容**：部分接口无 code 字段，前端已兼容
3. **图片/视频 base64**：注意格式、跨域、最大长度
4. **流释放**：切换摄像头/停止流时务必释放，防止内存泄漏
5. **按钮状态联动**：录制/检测/流切换等按钮状态需及时刷新
6. **调试技巧**：多用控制台打印、调试区块追踪状态
7. **样式自定义**：如需自定义主题，建议在 styles/element/dark.scss 修改
8. **组件导入**：建议继续使用 @/components、@/views 路径别名
9. **大文件上传**：建议分片或限制大小，防止前端卡死
10. **接口文档**：详见 API_Documentation.md、API_Interface_Documentation.md

---

## 调试与排查技巧

- 在页面加入调试区块，实时显示 recording、watching、selectedModel、selectedCamera 等状态
- 控制台打印接口返回内容，辅助定位问题
- 按钮状态、流释放、模型信息渲染等问题均可通过调试输出和状态追踪定位
- 如遇布局错乱，优先检查 flex、百分比、媒体查询设置

---

## 附录：接口文档与返回结构

详见根目录下：

- `API_Documentation.md`
- `API_Interface_Documentation.md`
- `Frontend_API_Guide.md`

如需补充接口参数、返回结构、示例代码，请参考上述文档或 src/api 目录下各 API 文件。

---

## 联系与支持

如有问题请联系前端负责人或查阅上述文档，欢迎反馈与建议！

---

如需进一步细化某一部分（如具体接口参数、某页面源码讲解、样式细节等），请告知！ 