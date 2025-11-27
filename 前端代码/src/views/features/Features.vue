<!--
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-04-28 19:25:47
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-04-28 19:38:54
 * @FilePath: \front\src\views\features\Features.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
  <div class="features-page">
    <CommonHeader :currentTime="currentTime" :theme="theme" @toggleTheme="toggleTheme" />
    <div class="main-content">
      <CommonSidebar />
      <div class="main">
        <CommonBreadcrumb :paths="breadcrumbPaths" />
        <div class="features-list">
          <div
            class="feature-card"
            :class="{ active: currentFeature === 'detection' }"
            @click="currentFeature = 'detection'"
          >
            <el-icon><i class="el-icon-search" /></el-icon>
            <div class="feature-title">智能检测</div>
            <div class="feature-desc">基于YOLO的智能目标检测</div>
          </div>
          <div
            class="feature-card"
            :class="{ active: currentFeature === 'training' }"
            @click="currentFeature = 'training'"
          >
            <el-icon><i class="el-icon-cpu" /></el-icon>
            <div class="feature-title">智能训练</div>
            <div class="feature-desc">上传数据集，配置参数，发起YOLO模型训练</div>
          </div>
        </div>
        <div class="feature-content">
          <EnhancedDetection v-if="currentFeature === 'detection'" title="智能检测" description="基于YOLO的智能目标检测系统" featureKey="detection" />
          <SmartTraining v-else />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import CommonHeader from '@/components/CommonHeader.vue';
import CommonSidebar from '@/components/CommonSidebar.vue';
import CommonBreadcrumb from '@/components/CommonBreadcrumb.vue';
import EnhancedDetection from './EnhancedDetection.vue';
import SmartTraining from './SmartTraining.vue';

const currentTime = ref(new Date().toLocaleString());
const theme = ref('dark');
const currentFeature = ref<'detection' | 'training'>('detection');
const breadcrumbPaths = computed(() => [
  { name: '首页', path: '/' },
  { name: '功能详情', path: '/features' },
  { name: currentFeature.value === 'detection' ? '智能检测' : '智能训练', path: '' }
]);
setInterval(() => {
  currentTime.value = new Date().toLocaleString();
}, 1000);
</script>

<style scoped>
.features-list {
  display: flex;
  gap: 32px;
  margin-bottom: 32px;
}
.feature-card {
  background: #23263a;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  padding: 32px 36px;
  cursor: pointer;
  transition: box-shadow 0.2s, border 0.2s;
  border: 2px solid transparent;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 220px;
}
.feature-card.active, .feature-card:hover {
  border: 2px solid #409eff;
  box-shadow: 0 4px 16px rgba(64,158,255,0.15);
}
.feature-title {
  font-size: 20px;
  font-weight: bold;
  color: #fff;
  margin-top: 12px;
}
.feature-desc {
  color: #b0b0b0;
  margin-top: 8px;
  font-size: 15px;
  text-align: center;
}
.feature-content {
  margin-top: 16px;
}
</style> 