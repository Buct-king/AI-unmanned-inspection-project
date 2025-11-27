# 无人巡检系统

## 项目简介
这是一个基于 Vue 3 + Spring Boot 的无人巡检系统，用于监控和管理工业设备的运行状态。系统提供实时设备状态监控、报警管理、趋势分析等功能。

## 技术栈

### 前端技术栈
- **框架**: Vue 3 + TypeScript
- **UI 组件库**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router
- **HTTP 客户端**: Axios
- **图表库**: ECharts
- **构建工具**: Vite
- **样式**: SCSS
- **工具库**:
  - crypto-js: 用于密码加密
  - dayjs: 日期处理
  - lodash: 工具函数

### 后端技术栈
- **框架**: Spring Boot
- **数据库**: MySQL
- **ORM**: MyBatis
- **安全框架**: Spring Security
- **JWT**: 用于身份认证
- **跨域处理**: CORS 配置
- **API 文档**: Swagger

## 功能特性

### 1. 用户管理
- 用户登录/登出
- 密码修改（MD5加密）
- 个人信息管理

### 2. 设备监控
- 实时设备状态展示
- 设备状态筛选（正常/异常）
- 分页查询
- 自动刷新（30秒间隔）

### 3. 报警管理
- 报警趋势分析
- 报警详情查看
- 报警处理状态跟踪
- 报警等级分类

### 4. 数据统计
- 设备运行状态统计
- 报警处理情况统计
- 趋势图表展示

## 项目结构

### 前端目录结构
```
src/
├── api/            # API 接口定义
├── assets/         # 静态资源
├── components/     # 公共组件
├── router/         # 路由配置
├── stores/         # Pinia 状态管理
├── styles/         # 全局样式
├── types/          # TypeScript 类型定义
└── views/          # 页面组件
```

### 后端目录结构
```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── cn/lqz/unmannedinspectionsystem/
│   │   │       ├── config/        # 配置类
│   │   │       ├── controller/    # 控制器
│   │   │       ├── service/       # 服务层
│   │   │       ├── mapper/        # MyBatis 映射
│   │   │       ├── entity/        # 实体类
│   │   │       └── util/          # 工具类
│   │   └── resources/
│   │       ├── application.yml    # 应用配置
│   │       └── mapper/            # MyBatis XML
└── pom.xml                        # Maven 配置
```

## 开发环境要求
- Node.js >= 16
- JDK >= 1.8
- MySQL >= 5.7
- Maven >= 3.6

## 安装和运行

### 前端
```bash
# 安装依赖
npm install

# 开发环境运行
npm run dev

# 生产环境构建
npm run build
```

### 后端
```bash
# 编译打包
mvn clean package

# 运行
java -jar target/unmanned-inspection-system.jar
```

## 配置说明

### 前端配置
- 环境变量配置在 `.env` 文件中
- API 基础路径配置在 `vite.config.ts` 中

### 后端配置
- 数据库配置在 `application.yml` 中
- JWT 配置在 `application.yml` 中
- 跨域配置在 `CorsConfig.java` 中

## 安全特性
- 密码 MD5 加密存储
- JWT 身份认证
- 跨域安全配置
- 请求参数验证

## 性能优化
- 前端组件懒加载
- 数据分页查询
- 定时刷新机制
- 图表数据缓存

## 贡献指南
1. Fork 项目
2. 创建特性分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 许可证
MIT License
