<template>
  <div class="device-card" :class="statusClass">
    <div class="card-header">
      <span class="device-name">{{ device.name }}</span>
      <el-tag :type="tagType" size="small">{{ statusText }}</el-tag>
    </div>
    <div class="card-content">
      <div class="device-image">
        <img :src="device.imageUrl || defaultImage" :alt="device.name">
      </div>
      <div class="device-info">
        <p class="location">{{ device.description }}</p>
        <p class="time">{{ device.lastUpdate }}</p>
      </div>
    </div>
    <div class="card-footer">
      <el-button-group>
        <el-button type="primary" size="small" @click="handleDetail">详情</el-button>
        <el-button type="success" size="small" @click="handleMonitor">监控</el-button>
      </el-button-group>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRouter } from 'vue-router';

interface DeviceStatus {
  mpId: number;
  name: string;
  description: string;
  imageUrl?: string;
  lastUpdate: string;
  status: 'alarm' | 'warning' | 'normal' | 'offline' | 'shutdown';
}

const props = defineProps<{
  device: DeviceStatus;
}>();

const router = useRouter();
const defaultImage = '/images/devices/default.jpg';

const statusClass = computed(() => ({
  'status-alarm': props.device.status === 'alarm',
  'status-warning': props.device.status === 'warning',
  'status-normal': props.device.status === 'normal',
  'status-offline': props.device.status === 'offline',
  'status-shutdown': props.device.status === 'shutdown'
}));

const statusText = computed(() => {
  const statusMap = {
    alarm: '报警',
    warning: '预警',
    normal: '正常',
    offline: '离线',
    shutdown: '停机'
  };
  return statusMap[props.device.status];
});

const tagType = computed(() => {
  const typeMap = {
    alarm: 'danger',
    warning: 'warning',
    normal: 'success',
    offline: 'info',
    shutdown: ''
  };
  return typeMap[props.device.status];
});

const handleDetail = () => {
  console.log('Device props:', props.device);
  console.log('Navigating to detail with mpId:', props.device.mpId);
  router.push(`/device-detail/${props.device.mpId}`);
};

const handleMonitor = () => {
  console.log('Device props:', props.device);
  console.log('Navigating to monitor with mpId:', props.device.mpId);
  router.push(`/device-monitor/${props.device.mpId}`);
};
</script>

<style scoped>
.device-card {
  background-color: #282b30;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  transition: all 0.3s ease;
  border: 1px solid #303030;
}

.device-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.status-alarm {
  border-left: 4px solid #ff4d4f;
}

.status-warning {
  border-left: 4px solid #faad14;
}

.status-normal {
  border-left: 4px solid #52c41a;
}

.status-offline {
  border-left: 4px solid #8c8c8c;
}

.status-shutdown {
  border-left: 4px solid #1890ff;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.device-name {
  font-size: 16px;
  font-weight: 500;
  color: #fff;
}

.card-content {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.device-image {
  width: 120px;
  height: 80px;
  border-radius: 4px;
  overflow: hidden;
}

.device-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.device-info {
  flex: 1;
}

.location {
  color: #8c8c8c;
  font-size: 14px;
  margin-bottom: 8px;
}

.time {
  color: #8c8c8c;
  font-size: 12px;
}

.card-footer {
  display: flex;
  justify-content: flex-end;
}
</style> 