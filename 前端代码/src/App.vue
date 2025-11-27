<!--
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-04-18 13:37:13
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-07-14 01:11:43
 * @FilePath: \front\src\App.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<script setup lang="ts">
import { useDark } from '@vueuse/core'
import { ElConfigProvider } from 'element-plus'
import { useUserStore } from '@/stores/user'

const isDark = useDark()
const userStore = useUserStore()
</script>

<template>
  <el-config-provider :dark="isDark">
    <div id="app" :class="{ 'dark': isDark }">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </div>
  </el-config-provider>
</template>

<style>
html {
  transition: all 0.3s ease;
}

html.dark {
  background-color: var(--el-bg-color-page);
  color: var(--el-text-color-primary);
}

#app {
  min-height: 100vh;
  background-color: var(--el-bg-color-page);
  color: var(--el-text-color-primary);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
