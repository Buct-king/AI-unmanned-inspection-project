/*
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-04-19 22:40:16
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-05-02 11:04:42
 * @FilePath: \front\src\main.ts
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
/*
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-04-19 22:40:16
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-04-19 22:43:20
 * @FilePath: \front\src\main.ts
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import './styles/element/dark.scss'
import './styles/base.scss'
import App from './App.vue'
import router from './router'
import type { App as VueApp } from 'vue'
import Video from 'video.js'
import 'video.js/dist/video-js.css'

const app: VueApp = createApp(App)
const pinia = createPinia()

// 使用持久化插件
pinia.use(piniaPluginPersistedstate)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 使用暗黑模式
app.use(ElementPlus as any, {
  darkMode: true,
})
app.use(router)
app.use(pinia)
app.config.globalProperties.$video = Video

// 禁用console.log输出
if (process.env.NODE_ENV === 'production') {
  console.log = () => {};
  // 如果需要禁用其他console方法
  // console.info = () => {};
  // console.warn = () => {};
  // console.error = () => {};
}

app.mount('#app') 