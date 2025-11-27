# 前端环境配置修改说明

本指南适用于将本前端项目交接给其他开发者或对接不同后端环境时，**需要修改的所有关键配置文件和字段**，确保前端能正确访问后端服务、接口和资源。

---

## 1. 需要修改的前端文件一览

| 文件路径                | 作用/说明                         | 需关注字段/内容           |
|------------------------|-----------------------------------|--------------------------|
| src/api/index.ts       | 通用/基础API的 baseURL 配置        | baseURL                  |
| src/api/yolo.ts        | 检测相关API的 baseURL 配置         | baseURL                  |
| src/api/training.ts    | 训练相关API的 baseURL 配置         | baseURL                  |
| vite.config.js         | 本地开发跨域代理配置               | server.proxy             |
| .env/.env.development  | （如有）全局环境变量配置           | VITE_API_BASE_URL 等      |

---

## 2. 具体修改方法

### 2.1 API 地址（baseURL）

前端所有与后端交互的 API 文件（如 `src/api/index.ts`、`src/api/yolo.ts`、`src/api/training.ts`）中，通常有如下配置：

```ts
const baseURL = 'http://localhost:8080/api'; // 端口和路径需与后端一致

const service = axios.create({
  baseURL,
  // ...其他配置
});
```

**如后端服务地址、端口、接口前缀有变动，需同步修改 baseURL。**

### 2.2 本地开发代理（vite.config.js）

本地开发时，前端端口（如 5173）和后端端口（如 8080）不同，需配置代理解决跨域：

```js
export default defineConfig({
  server: {
    proxy: {
      '/api': 'http://localhost:8080', // 端口和后端一致
    }
  }
});
```

**如后端端口或接口前缀有变动，需同步修改此处。**

### 2.3 环境变量（如有 .env 文件）

有些项目用 .env 文件统一管理 API 地址：

```
VITE_API_BASE_URL=http://localhost:8080/api
```

此时，API 文件中用 `import.meta.env.VITE_API_BASE_URL` 读取。

---

## 3. 其它注意事项

- **静态资源访问**：前端访问图片、模型等静态资源时，URL 需与后端接口返回的路径拼接，不要写死本地物理路径。
- **token 传递**：如后端鉴权方式有变，需同步调整前端 token 获取与传递逻辑。
- **接口路径变动**：如后端接口路径有调整，前端所有相关 API 路径需同步修改。
- **部署环境**：生产环境部署时，baseURL 需指向实际后端服务器地址。

---

## 4. 交接建议

- 交接时请务必告知后端服务的实际地址、端口、接口前缀。
- 建议将本说明文档一同交付，便于新开发者快速定位和修改配置。
- 如有疑问，优先查阅本项目 docs/frontend-dev-guide.md 和本文件。

---

如需进一步细化某一部分配置说明，请补充具体需求！ 