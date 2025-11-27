<!--
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-05-03 17:10:15
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-05-03 17:25:59
 * @Description: 设备详情页
-->
<template>
  <div class="device-detail dark-theme">
    <CommonHeader :currentTime="currentTime" :theme="theme" @toggleTheme="toggleTheme" />
    <div class="main-content">
      <CommonSidebar :isCollapse="isCollapse" />
      <div class="main">
        <CommonBreadcrumb :paths="breadcrumbPaths" />
        
        <div class="content-section" v-loading="loading">
          <div class="detail-layout">
            <!-- 左侧设备信息 -->
            <div class="device-info">
              <div class="info-header">
                <h2>{{ deviceDetail?.deviceName || '设备详情' }}</h2>
                <el-tag :type="deviceDetail?.measuringPointStatus === 0 ? 'success' : 'danger'">
                  {{ deviceDetail?.measuringPointStatus === 0 ? '正常' : '异常' }}
                </el-tag>
              </div>

              <div class="device-image">
                <img :src="deviceDetail?.base64Image" :alt="deviceDetail?.deviceName" v-if="deviceDetail?.base64Image">
                <el-empty description="暂无图片" v-else></el-empty>
              </div>

              <div class="info-content">
                <div class="info-item">
                  <span class="label">设备名称：</span>
                  <span class="value">{{ deviceDetail?.deviceName }}</span>
                </div>
                <div class="info-item">
                  <span class="label">所属车间：</span>
                  <span class="value">{{ deviceDetail?.workshopName }}</span>
                </div>
                <div class="info-item">
                  <span class="label">测点名称：</span>
                  <span class="value">{{ deviceDetail?.mpName }}</span>
                </div>
                <div class="info-item">
                  <span class="label">最后更新：</span>
                  <span class="value">{{ deviceDetail?.lastUpdateTime }}</span>
                </div>
              </div>
            </div>

            <!-- 右侧报警记录 -->
            <div class="alarm-logs">
              <div class="logs-header">
                <h3>报警记录</h3>
                <el-tag type="danger" v-if="activeAlarms.length">
                  {{ activeAlarms.length }} 个未处理报警
                </el-tag>
              </div>

              <el-table :data="deviceDetail?.mpAlarmList || []" style="width: 100%" size="small">
                <el-table-column prop="alarmTime" label="报警时间" width="180" />
                <el-table-column prop="typeString" label="报警状态" width="120">
                  <template #default="{ row }">
                    <el-tag type="danger">{{ row.typeString }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="status" label="处理状态" width="120">
                  <template #default="{ row }">
                    <el-tag :type="row.status === 0 ? 'warning' : 'success'">
                      {{ row.status === 0 ? '未处理' : '已处理' }}
                    </el-tag>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import CommonHeader from '@/components/CommonHeader.vue';
import CommonSidebar from '@/components/CommonSidebar.vue';
import CommonBreadcrumb from '@/components/CommonBreadcrumb.vue';
import { mpApi } from '@/api';
import { ElMessage } from 'element-plus';

const route = useRoute();
const currentTime = ref(new Date().toLocaleString());
const theme = ref('dark');
const isCollapse = ref(false);
const loading = ref(false);
const deviceDetail = ref(null);

const breadcrumbPaths = computed(() => [
  { name: '首页', path: '/' },
  { name: '状态监控', path: '/status-monitoring' },
  { name: '设备详情', path: '' }
]);

// 计算活跃的报警数量
const activeAlarms = computed(() => {
  if (!deviceDetail.value?.mpAlarmList) return [];
  return deviceDetail.value.mpAlarmList.filter(alarm => alarm.status === 0);
});

const getDeviceDetail = async () => {
  loading.value = true;
  try {
    const mpId = Number(route.params.id);
    console.log('Route params:', route.params);
    console.log('Parsed mpId:', mpId);
    const res = await mpApi.getDetail({ mpId });
    console.log('API request params:', { mpId });
    console.log('API response:', res);
    if (res.code === 200) {
      // 处理base64图片
      if (res.data.base64Image) {
        res.data.base64Image = res.data.base64Image.startsWith('data:image')
          ? res.data.base64Image
          : `data:image/jpeg;base64,${res.data.base64Image}`;
      }
      deviceDetail.value = res.data;
    } else {
      ElMessage.error(res.info || '获取设备详情失败');
    }
  } catch (error) {
    console.error('获取设备详情失败:', error);
    ElMessage.error('获取设备详情失败');
  } finally {
    loading.value = false;
  }
};

const toggleTheme = () => {
  theme.value = theme.value === 'dark' ? 'light' : 'dark';
  const html = document.documentElement;
  if (theme.value === 'dark') {
    html.classList.add('dark');
  } else {
    html.classList.remove('dark');
  }
};

onMounted(() => {
  getDeviceDetail();
});
</script>

<style scoped>
.device-detail {
  min-height: 100vh;
  background-color: #1a1c1e;
}

.main-content {
  display: flex;
  min-height: calc(100vh - 60px);
}

.main {
  flex: 1;
  padding: 20px;
  background-color: #1a1c1e;
}

.content-section {
  background-color: #282b30;
  border-radius: 8px;
  padding: 24px;
}

.detail-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

/* 左侧设备信息样式 */
.device-info {
  background-color: #1a1c1e;
  border-radius: 8px;
  padding: 24px;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.info-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 500;
  color: #fff;
}

.device-image {
  width: 100%;
  height: 300px;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 24px;
  background-color: #282b30;
  display: flex;
  align-items: center;
  justify-content: center;
}

.device-image img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.info-content {
  display: grid;
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
}

.info-item .label {
  color: #8c8c8c;
  width: 100px;
}

.info-item .value {
  color: #fff;
}

/* 右侧报警记录样式 */
.alarm-logs {
  background-color: #1a1c1e;
  border-radius: 8px;
  padding: 24px;
}

.logs-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.logs-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
  color: #fff;
}

:deep(.el-table) {
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  --el-table-border-color: #303030;
  --el-table-header-bg-color: #282b30;
  --el-table-header-text-color: #fff;
  --el-table-text-color: #fff;
}

:deep(.el-table th.el-table__cell) {
  background-color: #282b30;
}

:deep(.el-table--border .el-table__inner-wrapper::after),
:deep(.el-table--border::after),
:deep(.el-table--border::before) {
  background-color: #303030;
}
</style> 