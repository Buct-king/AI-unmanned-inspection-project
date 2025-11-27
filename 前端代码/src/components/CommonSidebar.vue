<template>
  <div class="sidebar-container">
    <el-menu
      :default-active="activeMenu"
      class="el-menu-vertical"
      background-color="#282b30"
      text-color="#8c8c8c"
      active-text-color="#fff"
      :collapse="isCollapse"
      router
    >
      <el-menu-item v-for="item in singleMenuItems" :key="item.path" :index="item.path">
        <el-icon><component :is="item.icon" /></el-icon>
        <template #title>
          <span>{{ item.label }}</span>
        </template>
      </el-menu-item>
      <el-sub-menu index="/features">
        <template #title>
          <el-icon><Operation /></el-icon>
          <span>功能详情</span>
        </template>
        <el-menu-item v-for="subItem in featureMenuItems" :key="subItem.path" :index="subItem.path">
          <el-icon><component :is="subItem.icon" /></el-icon>
          <span>{{ subItem.label }}</span>
        </el-menu-item>
      </el-sub-menu>
    </el-menu>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRoute } from 'vue-router';
import { 
  HomeFilled, 
  Monitor, 
  Bell, 
  Setting,
  Operation,
  PictureFilled
} from '@element-plus/icons-vue';

const route = useRoute();
const activeMenu = computed(() => route.path);

const singleMenuItems = ref([
  { path: '/', label: '首页', icon: 'HomeFilled' },
  { path: '/workspace', label: '工作台', icon: 'HomeFilled' },
  { path: '/status-monitoring', label: '状态监控', icon: 'Monitor' },
  { path: '/alarm-management', label: '报警管理', icon: 'Bell' },
  { path: '/system-management', label: '系统管理', icon: 'Setting' },
  { path: '/camera-stream', label: '摄像头实时流', icon: 'Monitor' },
  { path: '/video-gallery', label: '检测后视频', icon: 'PictureFilled' },
  { path: '/camera-model-bind', label: '摄像头模型绑定', icon: 'Setting' }
]);

const featureMenuItems = ref([
  { path: '/smart-detect', label: '智能检测', icon: 'PictureFilled' },
  { path: '/smart-training', label: '智能训练', icon: 'PictureFilled' }
]);

defineProps({
  isCollapse: {
    type: Boolean,
    default: false
  }
});
</script>

<style scoped>
.sidebar-container {
  height: 100%;
  background-color: #282b30;
}

.el-menu-vertical {
  border-right: none;
  height: 100%;
}

.el-menu-vertical:not(.el-menu--collapse) {
  width: 200px;
}

:deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  margin: 4px 0;
}

:deep(.el-menu-item.is-active) {
  background-color: #1a1c1e !important;
  border-right: 2px solid var(--el-color-primary);
}

:deep(.el-menu-item:hover) {
  background-color: #1a1c1e !important;
}

:deep(.el-menu-item .el-icon) {
  font-size: 18px;
}

:deep(.el-sub-menu__title) {
  color: #8c8c8c !important;
}

:deep(.el-sub-menu__title:hover) {
  background-color: #1a1c1e !important;
}

:deep(.el-sub-menu.is-active .el-sub-menu__title) {
  color: #fff !important;
}
</style>