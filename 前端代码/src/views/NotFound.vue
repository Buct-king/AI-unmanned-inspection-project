<!--
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-07-05 19:08:50
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-07-06 15:47:01
 * @FilePath: \front_flask\src\views\NotFound.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <h1>404</h1>
      <p>抱歉，您访问的页面不存在。</p>
      <el-button type="primary" @click="goHome">返回首页</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
const router = useRouter();
const goHome = () => {
  router.push('/home');
};
</script>

<style scoped>
.not-found-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #181a1b;
}
.not-found-content {
  text-align: center;
  color: #fff;
}
.not-found-content h1 {
  font-size: 96px;
  margin-bottom: 24px;
  color: #409eff;
}
.not-found-content p {
  font-size: 20px;
  margin-bottom: 32px;
}
</style> 