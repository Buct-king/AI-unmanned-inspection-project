/*
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-04-30 15:43:30
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-07-15 00:17:04
 * @FilePath: \front\vite.config.js
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
/*
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-04-30 15:43:30
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-05-02 15:53:01
 * @FilePath: \front\vite.config.js
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

// https://vite.dev/config/
import { dirname } from 'path';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 8081,
    proxy: {
      '/api': {
        // target: 'http://10.4.115.17:8080',修改这里
        target: 'http://localhost:8080',
        changeOrigin: true,
        ws: true,
        secure: false,
        onProxyReq: (proxyReq, req) => {
          console.log('Proxying to:', proxyReq.path);
        }
      }
    },
    hmr: {
      protocol: 'ws',
      host: 'localhost',
      port: 8081
    }
  }
})
