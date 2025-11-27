<template>
  <div class="alarm-management dark-theme">
    <CommonHeader :currentTime="currentTime" :theme="theme" @toggleTheme="toggleTheme" />
    <div class="main-content">
      <CommonSidebar :isCollapse="isCollapse" />
      <div class="main">
        <CommonBreadcrumb :paths="[{name: '首页', path: '/'}, {name: '报警管理', path: '/alarm-management'}]" />
        
        <div class="content-section">
          <div class="section-header">
            <div class="filter-section">
              <div class="filter-container">
                <div class="filter-items">
                  <el-date-picker
                    v-model="dateRange"
                    type="daterange"
                    range-separator="至"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    class="filter-item"
                  />
                  <el-select v-model="deviceType" placeholder="设备类型" class="filter-item">
                    <el-option
                      v-for="option in deviceTypeOptions"
                      :key="option.value"
                      :label="option.label"
                      :value="option.value"
                    />
                  </el-select>
                  <el-select v-model="status" placeholder="处理状态" class="filter-item">
                    <el-option
                      v-for="option in statusOptions"
                      :key="option.value"
                      :label="option.label"
                      :value="option.value"
                    />
                  </el-select>
                  <div class="filter-buttons">
                    <el-button type="primary" @click="query">查询</el-button>
                    <el-button @click="reset">重置</el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="status-cards">
            <div class="status-card">
              <div class="card-icon warning">
                <el-icon><Warning /></el-icon>
              </div>
              <div class="card-content">
                <h3>未处理报警</h3>
                <div class="main-value">{{ alarmData.filter(item => item.status === 0).length }}</div>
              </div>
            </div>
            <div class="status-card">
              <div class="card-icon completed">
                <el-icon><CircleCheckFilled /></el-icon>
              </div>
              <div class="card-content">
                <h3>已处理报警</h3>
                <div class="main-value">{{ alarmData.filter(item => item.status === 1).length }}</div>
              </div>
            </div>
          </div>

          <el-table 
            :data="alarmData" 
            style="width: 100%"
            class="custom-table"
            v-loading="loading"
          >
            <el-table-column type="index" label="序号" width="70" />
            <el-table-column prop="deviceId" label="设备ID" width="100" />
            <el-table-column prop="deviceName" label="设备名称">
              <template #default="{ row }">
                {{ row.deviceName || '未知设备' }}
              </template>
            </el-table-column>
            <el-table-column prop="mpId" label="测点ID" width="100" />
            <el-table-column prop="typeString" label="报警类型">
              <template #default="{ row }">
                {{ row.typeString || '无' }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="处理状态" width="120">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ getStatusLabel(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="alarmTime" label="报警时间" width="180" />
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button 
                  type="primary" 
                  link 
                  @click="markAsProcessed(row.alarmId)"
                  :disabled="row.status === 1"
                >
                  {{ row.status === 1 ? '已处理' : '标记已处理' }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-container">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="total"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { Warning, Loading, CircleCheckFilled } from '@element-plus/icons-vue';
import CommonHeader from '@/components/CommonHeader.vue';
import CommonSidebar from '@/components/CommonSidebar.vue';
import CommonBreadcrumb from '@/components/CommonBreadcrumb.vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { alarmApi } from '@/api';

const currentTime = ref(new Date().toLocaleString());
const theme = ref('dark');
const isCollapse = ref(false);
// 设备类型 整形
const deviceType = ref(-1);
// 处理状态 整形
const status = ref(-1);
// 日期范围
const dateRange = ref([]);
// 当前页
const currentPage = ref(1);
// 每页大小
const pageSize = ref(10);
const total = ref(100);
const loading = ref(false);

const alarmCount = ref({
  pending: 156,
  processing: 23,
  completed: 892
});

const alarmData = ref([
]);

const router = useRouter();

// 设备类型选项
const deviceTypeOptions = [
  { label: '全部', value: -1 },
  { label: '设备0', value: 0 },
  { label: '设备1', value: 1 },
  { label: '设备2', value: 2 },
  { label: '设备3', value: 3 }
];

// 处理状态选项
const statusOptions = [
  { label: '全部', value: -1 },
  { label: '已处理', value: 1 },
  { label: '未处理', value: 0 }
];


// 获取状态描述
const getStatusLabel = (statusValue) => {
  return statusOptions.find(option => option.value === statusValue)?.label || '未知状态';
};

// 获取状态对应的类型（用于标签样式）
const getStatusType = (statusValue) => {
  const types = {
    1: 'success',   // 已处理
    0: 'danger'     // 未处理
  };
  return types[statusValue] || 'info';
};

// 定时器
let dataTimer = null;
const REFRESH_INTERVAL = 30000; // 30秒刷新一次

const statusItems = ref([
  { type: 'normal', label: '正常运行', value: 70, count: 70 },
  { type: 'warning', label: '报警设备', value: 10, count: 10 },
  { type: 'pending', label: '未处理', value: 12, count: 12 },
  { type: 'success', label: '已处理', value: 8, count: 8 }
]);

// 获取报警状态统计
const fetchStatusCount = async () => {
  try {
    const res = await alarmApi.getStatusCount();
    if (res.code === 200) {
      // 更新状态卡片数据
      statusItems.value = [
        { type: 'normal', label: '正常运行', value: 70, count: 70 },
        { type: 'warning', label: '报警设备', value: 10, count: 10 },
        { type: 'pending', label: '未处理', value: res.data.unprocessed, count: res.data.unprocessed },
        { type: 'success', label: '已处理', value: res.data.processed, count: res.data.processed }
      ];
    }
  } catch (error) {
    // console.error('获取报警状态统计失败:', error);
    ElMessage.error('获取数据失败');
  }
};

// 获取报警数据
const fetchAlarmData = async () => {
  loading.value = true;
  try {
    const [listRes, statsRes] = await Promise.all([
      alarmApi.loadList({
        pageNo: currentPage.value,
        pageSize: pageSize.value,
        deviceType: deviceType.value === -1 ? undefined : deviceType.value,
        status: status.value === -1 ? undefined : status.value,
        startTime: dateRange.value?.[0]?.format('YYYY-MM-DD HH:mm:ss'),
        endTime: dateRange.value?.[1]?.format('YYYY-MM-DD HH:mm:ss')
      }),
      alarmApi.getStatusCount()  // 添加获取状态统计
    ]);

    if (listRes.code === 200) {
      alarmData.value = listRes.data.list;
      total.value = listRes.data.pageTotal;
      currentPage.value = listRes.data.pageNo;
      pageSize.value = listRes.data.pageSize;
    }

    if (statsRes.code === 200) {
      // 更新状态卡片数据
      statusItems.value = [
        { type: 'normal', label: '正常运行', value: 70, count: 70 },
        { type: 'warning', label: '报警设备', value: 10, count: 10 },
        { type: 'pending', label: '未处理', value: statsRes.data.unprocessed, count: statsRes.data.unprocessed },
        { type: 'success', label: '已处理', value: statsRes.data.processed, count: statsRes.data.processed }
      ];
    }
  } catch (error) {
    // console.error('获取报警数据失败:', error);
    ElMessage.error('获取数据失败');
  } finally {
    loading.value = false;
  }
};

// 开始定时刷新
const startAutoRefresh = () => {
  // 清除可能存在的旧定时器
  stopAutoRefresh();

  // 设置新的定时器
  dataTimer = window.setInterval(() => {
    fetchAlarmData();
  }, REFRESH_INTERVAL);
};

// 停止定时刷新
const stopAutoRefresh = () => {
  if (dataTimer) {
    clearInterval(dataTimer);
    dataTimer = null;
  }
};

// 组件挂载时获取数据并开始定时刷新
onMounted(() => {
  fetchAlarmData();
  startAutoRefresh();
});

// 组件卸载时清理定时器
onBeforeUnmount(() => {
  stopAutoRefresh();
});

// 查询方法
const query = () => {
  fetchAlarmData();
};

// 重置方法
const reset = () => {
  deviceType.value = -1;
  status.value = -1;
  dateRange.value = [];
  currentPage.value = 1;
  pageSize.value = 10;
  query();
};

// 分页方法
const handleSizeChange = (val) => {
  pageSize.value = val;
  query();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  query();
};

const handleAlarmDetail = (row) => {
  router.push({
    path: `/device-detail/${row.deviceId}`,
    query: { from: '/alarm-management' }
  });
};

const handleProcess = (row) => {
  router.push(`/alarm-process/${row.id}`);
};


const markAsProcessed = async (alarmId) => {
  try {
    const res = await alarmApi.handleAlarm(alarmId);
    if (res.code === 200) {
      ElMessage.success('报警已标记为已处理');
      // 刷新数据
      fetchAlarmData();
    } else {
      ElMessage.error(res.info || '处理失败');
    }
  } catch (error) {
    console.error('处理报警失败:', error);
    ElMessage.error('处理失败，请稍后重试');
  }
};
</script>

<style scoped>
.alarm-management {
  min-height: 100vh;
  background-color: #1a1c1e;
  display: flex;
  flex-direction: column;
}

.main-content {
  display: flex;
  flex: 1;
  min-height: calc(100vh - 60px);
}

.main {
  flex: 1;
  padding: 2%;
  background-color: #1a1c1e;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.content-section {
  background-color: #282b30;
  border-radius: 8px;
  padding: 2%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  font-size: 16px;
  color: #fff;
  margin: 0;
}

.filter-section {
  width: 100%;
}

.filter-container {
  width: 100%;
}

.filter-items {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.filter-buttons {
  display: flex;
  gap: 12px;
  margin-left: 12px;
}

.filter-item {
  width: 180px;
}

.status-cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 2%;
  margin-bottom: 20px;
}

.status-card {
  background-color: #1a1c1e;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.status-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.card-icon.warning {
  background-color: rgba(255, 77, 79, 0.1);
  color: #ff4d4f;
}

.card-icon.completed {
  background-color: rgba(82, 196, 26, 0.1);
  color: #52c41a;
}

.card-content {
  flex: 1;
}

.card-content h3 {
  color: #8c8c8c;
  font-size: 14px;
  margin: 0 0 8px 0;
}

.main-value {
  font-size: 24px;
  font-weight: bold;
  color: #fff;
}

.custom-table {
  margin-bottom: 20px;
}

:deep(.el-table) {
  background-color: transparent;
}

:deep(.el-table__empty-block) {
  background-color: #282b30;
  min-height: 60px;
}

:deep(.el-table__empty-text) {
  color: #909399;
}

.pagination-container {
  padding-top: 20px;
  display: flex;
  justify-content: flex-end;
}

@media screen and (max-width: 1200px) {
  .filter-item {
    width: 150px;
  }
}

@media screen and (max-width: 768px) {
  .status-cards {
    grid-template-columns: 1fr;
  }
  
  .filter-items {
    flex-direction: column;
  }
  
  .filter-item {
    width: 100%;
  }
}
</style>