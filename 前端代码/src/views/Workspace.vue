<template>
  <div class="workspace dark-theme">
    <CommonHeader />
    <div class="main-content">
      <CommonSidebar :isCollapse="isCollapse" @open="handleOpen" @close="handleClose" />
      <div class="main">
        <CommonBreadcrumb />
        <div class="summary">
          <div class="summary-card" :class="'status-' + item.type" v-for="item in statusItems" :key="item.type">
            <div class="card-icon">
              <el-icon v-if="item.type === 'normal'"><Monitor /></el-icon>
              <el-icon v-else-if="item.type === 'warning'"><Warning /></el-icon>
              <el-icon v-else-if="item.type === 'pending'"><Bell /></el-icon>
              <el-icon v-else-if="item.type === 'success'"><CircleCheckFilled /></el-icon>
            </div>
            <div class="card-content">
              <h3>{{ item.label }}</h3>
              <div class="main-value">{{ item.count }} 台</div>
            </div>
          </div>
        </div>

        <div class="content-section">
          <div class="section-header">
            <h3>设备状态</h3>
            <div class="filter-section">
              <el-select v-model="statusFilter" placeholder="检测状态" class="filter-item">
                <el-option label="全部" value="" />
                <el-option label="正常" :value="0" />
                <el-option label="异常" :value="1" />
              </el-select>
              <div class="filter-buttons">
                <el-button type="primary" @click="handleSearch">查询</el-button>
                <el-button @click="handleReset">重置</el-button>
              </div>
            </div>
          </div>
          
          <el-table 
            :data="deviceStatusData" 
            style="width: 100%"
            class="custom-table"
            v-loading="loading"
          >
            <el-table-column type="index" label="序号" width="70" />
            <el-table-column prop="deviceName" label="设备名称" />
            <el-table-column prop="measuringPointName" label="测点名称" />
            <el-table-column prop="measuringPointStatus" label="检测状态">
              <template #default="{ row }">
                <el-tag :type="row.measuringPointStatus === 0 ? 'success' : 'danger'">
                  {{ row.measuringPointStatus === 0 ? '正常' : '异常' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="workshopName" label="车间">
              <template #default="{ row }">
                {{ row.workshopName || '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述" show-overflow-tooltip />
          </el-table>
          <div class="pagination-container">
            <span class="total-text">Total {{ total }}</span>
            <div class="page-control">
              <el-select v-model="pageSize" class="page-size-select">
                <el-option
                  :value="12"
                  label="12/page"
                />
              </el-select>
              <div class="page-buttons">
                <el-button
                  :disabled="currentPage <= 1"
                  @click="currentPage--"
                >
                  <el-icon><ArrowLeft /></el-icon>
                </el-button>
                <span class="current-page">{{ currentPage }}</span>
                <el-button
                  :disabled="currentPage >= Math.ceil(total / pageSize)"
                  @click="currentPage++"
                >
                  <el-icon><ArrowRight /></el-icon>
                </el-button>
              </div>
            </div>
          </div>
        </div>

        

        <div class="chart-section">
          <div class="section-header">
            <h3>报警趋势</h3>
          </div>
          <ReusableChart :chartData="chartData" chartType="line" @click="handleChartClick" />
        </div>
      </div>
    </div>

    <!-- 图表详情弹窗 -->
    <el-dialog
      v-model="chartDetailVisible"
      :title="chartDetailTitle"
      width="800px"
      destroy-on-close
    >
      <div v-loading="detailLoading">
        <div class="detail-summary">
          <div class="detail-item">
            <span class="label">时间点：</span>
            <span class="value">{{ chartDetail.time }}</span>
          </div>
          <div class="detail-item">
            <span class="label">报警数量：</span>
            <span class="value highlight">{{ chartDetail.value }} 条</span>
          </div>
        </div>

        <el-divider>报警详情</el-divider>

        <el-table
          :data="chartDetail.alarms"
          style="width: 100%"
          class="custom-table"
          v-if="chartDetail.alarms?.length"
        >
          <el-table-column type="index" label="序号" width="70" />
          <el-table-column prop="deviceName" label="设备名称" />
          <el-table-column prop="alarmType" label="报警类型" />
          <el-table-column prop="level" label="报警等级">
            <template #default="{ row }">
              <el-tag :type="getAlarmLevelType(row.level)">
                {{ row.level }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="time" label="报警时间" />
          <el-table-column prop="status" label="处理状态">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">
                {{ row.status }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>

        <div v-else class="no-data">
          暂无报警详情数据
        </div>
      </div>
    </el-dialog>

    <!-- 报警详情弹窗 -->
    <el-dialog
      v-model="alarmDetailVisible"
      :title="alarmDetailTitle"
      width="800px"
      destroy-on-close
    >
      <div v-loading="detailLoading">
        <div class="detail-summary">
          <div class="detail-item">
            <span class="label">月份：</span>
            <span class="value">{{ alarmDetailData.month }}</span>
          </div>
          <div class="detail-item">
            <span class="label">报警数量：</span>
            <span class="value highlight">{{ alarmDetailData.count }} 条</span>
          </div>
        </div>

        <el-divider>报警详情</el-divider>

        <el-table
          :data="alarmDetailData.details"
          style="width: 100%"
          class="custom-table"
          v-if="alarmDetailData.details?.length"
        >
          <el-table-column type="index" label="序号" width="70" />
          <el-table-column prop="date" label="日期" />
          <el-table-column prop="type" label="报警类型" />
          <el-table-column prop="status" label="处理状态">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">
                {{ row.status }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>

        <div v-else class="no-data">
          暂无报警详情数据
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, onBeforeUnmount, computed } from 'vue';
import { Warning, Bell, Monitor, CircleCheckFilled, ArrowLeft, ArrowRight } from '@element-plus/icons-vue';
import ReusableChart from '@/components/ReusableChart.vue';
import CommonHeader from '@/components/CommonHeader.vue';
import CommonSidebar from '@/components/CommonSidebar.vue';
import CommonBreadcrumb from '@/components/CommonBreadcrumb.vue';
import * as echarts from 'echarts';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import { alarmApi, mpApi, deviceApi } from '@/api';
import { useUserStore } from '@/stores/user';

const isCollapse = ref(false);
const currentTime = ref(new Date().toLocaleString());
const theme = ref('dark');
const timeRange = ref('today');
const workspaceData = ref({});
const loading = ref(false);
const router = useRouter();

// 图表详情相关
const chartDetailVisible = ref(false);
const chartDetailTitle = ref('');
const chartDetail = ref({});
const detailLoading = ref(false);

// 筛选相关的响应式变量
const workshopFilter = ref('');
const deviceTypeFilter = ref('');
const statusFilter = ref('');
const searchKeyword = ref('');

// 定时器
let overviewTimer = null;
let alarmTimer = null;
let deviceStatusTimer = null;
const REFRESH_INTERVAL = 30000; // 30秒刷新一次

// 分页相关
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 设备状态数据
const deviceStatusData = ref([]);

// 状态概览数据
const statusItems = ref([
  { type: 'normal', label: '运行中', count: 0 },
  { type: 'warning', label: '报警', count: 0 },
  { type: 'pending', label: '未处理', count: 0 },
  { type: 'success', label: '已处理', count: 0 }
]);

// 添加弹窗相关的响应式变量
const alarmDetailVisible = ref(false);
const alarmDetailTitle = ref('');
const alarmDetailData = ref({
  month: '',
  count: 0,
  details: []
});

// 用户信息
const userStore = useUserStore();
const userInfo = computed(() => {
  return {
    name: userStore.currentUser?.name || '未登录',
    role: userStore.currentUser?.roleType || 0
  };
});

const handleOpen = (key, keyPath) => {
  console.log(key, keyPath);
};

const handleClose = (key, keyPath) => {
  console.log(key, keyPath);
};

const chartData = ref({
  xAxisData: ['2024-01', '2024-02', '2024-03', '2024-04', '2024-05', '2024-06'],
  yAxisData: [30, 40, 20, 50, 60, 70],
  options: {
    title: {
      text: '报警趋势分析',
      left: 'center',
      top: 10
    },
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['2024-01', '2024-02', '2024-03', '2024-04', '2024-05', '2024-06']
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      data: [30, 40, 20, 50, 60, 70],
      type: 'line',
      smooth: true,
      symbolSize: 8,
      lineStyle: {
        width: 3,
        color: '#0b82c6'
      },
      itemStyle: {
        color: '#0b82c6',
        borderWidth: 2,
        borderColor: '#fff'
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(11, 130, 198, 0.3)' },
          { offset: 1, color: 'rgba(11, 130, 198, 0.05)' }
        ])
      }
    }]
  }
});


const toggleTheme = () => {
  document.body.classList.toggle('dark-theme', theme.value === 'dark');
};

const handleDispose = (id) => {
  router.push(`/alarm-process/${id}`);
};

// 获取报警数据
const fetchAlarmData = async () => {
  try {
    const res = await alarmApi.countAlarmTimeByMonth();
    if (res.code === 200) {
      const data = res.data;
      // 更新图表数据
      chartData.value = {
        xAxisData: Object.keys(data),
        yAxisData: Object.values(data),
        options: {
          title: {
            text: '报警趋势分析',
            left: 'center',
            top: 10,
            textStyle: {
              color: '#fff'
            }
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
          },
          xAxis: {
            type: 'category',
            data: Object.keys(data),
            axisLine: {
              lineStyle: { color: '#8c8c8c' }
            },
            axisLabel: {
              color: '#8c8c8c',
              interval: 0,
              rotate: 30
            }
          },
          yAxis: {
            type: 'value',
            name: '报警数量',
            nameTextStyle: {
              color: '#8c8c8c'
            },
            axisLine: {
              lineStyle: { color: '#8c8c8c' }
            },
            splitLine: {
              lineStyle: { color: 'rgba(140, 140, 140, 0.2)' }
            },
            axisLabel: {
              color: '#8c8c8c'
            }
          },
          series: [{
            data: Object.values(data),
            type: 'line',
            smooth: true,
            symbolSize: 8,
            lineStyle: {
              width: 3,
              color: '#0b82c6'
            },
            itemStyle: {
              color: '#0b82c6',
              borderWidth: 2,
              borderColor: '#fff'
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(11, 130, 198, 0.3)' },
                { offset: 1, color: 'rgba(11, 130, 198, 0.05)' }
              ])
            }
          }]
        }
      };
    }
  } catch (error) {
    console.error('获取报警趋势数据失败:', error);
    ElMessage.error('获取报警趋势数据失败');
  }
};
// 获取设备状态数据
const fetchDeviceStatusData = async () => {
  try {
    const res = await mpApi.loadList({
      pageNo: currentPage.value,
      pageSize: pageSize.value,
      status: statusFilter.value === '' ? undefined : statusFilter.value
    });

    if (res.code === 200) {
      deviceStatusData.value = res.data.list;
      total.value = res.data.pageTotal;
    } else {
      ElMessage.error(res.info || '获取设备状态数据失败');
    }
  } catch (error) {
    console.error('获取设备状态数据失败:', error);
    ElMessage.error('获取设备状态数据失败');
  }
};

// 获取状态统计数据
const fetchStatusData = async () => {
  try {
    // 获取报警处理状态统计
    const alarmRes = await alarmApi.getStatusCount();
    if (alarmRes.code === 200) {
      const { processedCount, unprocessedCount } = alarmRes.data;
      
      // 更新已处理和未处理的数据
      statusItems.value[2].count = unprocessedCount;
      statusItems.value[3].count = processedCount;
    }

    // 获取设备状态统计
    const deviceRes = await  deviceApi.countStatus();
    if (deviceRes.code === 200) {
      const { runningCount, alarmCount } = deviceRes.data;
      
      // 更新运行中和报警的数据
      statusItems.value[0].count = runningCount;
      statusItems.value[1].count = alarmCount;
    }
  } catch (error) {
    console.error('获取状态统计数据失败:', error);
    ElMessage.error('获取状态统计数据失败');
  }
};

// 组件挂载时获取数据并开始定时刷新
onMounted(() => {
  fetchStatusData();
  fetchAlarmData();
  fetchDeviceStatusData();
  startAutoRefresh();

});

// 开始定时刷新
const startAutoRefresh = () => {
  // 清除可能存在的旧定时器
  stopAutoRefresh();

  // 设置新的定时器
  overviewTimer = window.setInterval(() => {
    fetchStatusData();
    fetchDeviceStatusData();
    fetchAlarmData();  // 添加报警趋势数据的定时刷新
  }, REFRESH_INTERVAL);
};

// 停止定时刷新
const stopAutoRefresh = () => {
  if (overviewTimer) {
    clearInterval(overviewTimer);
    overviewTimer = null;
  }
};

// 组件卸载时清理定时器
onBeforeUnmount(() => {
  stopAutoRefresh();
});

// 查询方法
const handleSearch = () => {
  currentPage.value = 1;  // 重置页码
  fetchDeviceStatusData();
};

// 重置方法
const handleReset = () => {
  statusFilter.value = '';
  currentPage.value = 1;
  fetchDeviceStatusData();
};

// 监听分页和状态筛选变化
watch([currentPage, pageSize, statusFilter], () => {
  fetchDeviceStatusData();
});

// 每页条数变化
const handleSizeChange = (val) => {
  pageSize.value = val;
  currentPage.value = 1;
  fetchDeviceStatusData();
};

// 跳转到登录页面
const goToLogin = () => {
  router.push('/login');
};

// 处理下拉菜单命令
const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile');
      break;
    case 'logout':
      try {
        await ElMessageBox.confirm(
          '确定要退出登录吗？',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        );
        
        // 清除用户状态
        userStore.clearUser();
        // 清除localStorage中的持久化数据
        localStorage.removeItem('user');
        
        // 跳转到登录页
        router.push('/login');
        
        ElMessage.success('已退出登录');
      } catch (error) {
        if (error !== 'cancel') {
          console.error('退出登录错误:', error);
          ElMessage.error('退出失败，请重试');
        }
      }
      break;
  }
};

</script>

<style scoped>
.workspace {
  min-height: 100vh;
  background-color: #1a1c1e;
  color: #fff;
}

.filter-item {
  width: 180px;
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

.summary {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.summary-card {
  background-color: #282b30;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.summary-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(45deg, rgba(255, 255, 255, 0.03), rgba(255, 255, 255, 0));
  opacity: 0;
  transition: opacity 0.3s ease;
}

.summary-card:hover::before {
  opacity: 1;
}

.summary-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  transition: all 0.3s ease;
}

.status-normal .card-icon {
  background-color: rgba(82, 196, 26, 0.1);
  color: #52c41a;
}

.status-warning .card-icon {
  background-color: rgba(250, 173, 20, 0.1);
  color: #faad14;
}

.status-pending .card-icon {
  background-color: rgba(255, 77, 79, 0.1);
  color: #ff4d4f;
}

.status-success .card-icon {
  background-color: rgba(64, 158, 255, 0.1);
  color: #409eff;
}

.card-content {
  flex: 1;
}

.card-content h3 {
  color: rgba(255, 255, 255, 0.65);
  font-size: 14px;
  margin: 0 0 8px 0;
  font-weight: normal;
  letter-spacing: 0.5px;
}

.main-value {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 4px;
  background: linear-gradient(45deg, #fff, rgba(255, 255, 255, 0.8));
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.status-normal .main-value {
  background: linear-gradient(45deg, #52c41a, #95de64);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.status-warning .main-value {
  background: linear-gradient(45deg, #faad14, #ffd666);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.status-pending .main-value {
  background: linear-gradient(45deg, #ff4d4f, #ff7875);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.status-success .main-value {
  background: linear-gradient(45deg, #409eff, #69b1ff);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.sub-value {
  font-size: 12px;
  color: #8c8c8c;
}

.content-section {
  background-color: #282b30;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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
  font-weight: 500;
}

.custom-table {
  border-radius: 8px;
  overflow: hidden;
}

.chart-section {
  background-color: #282b30;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.detail-summary {
  display: flex;
  gap: 40px;
  margin-bottom: 20px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-item .label {
  color: #8c8c8c;
}

.detail-item .value {
  color: #fff;
}

.detail-item .value.highlight {
  color: #ff4d4f;
  font-weight: bold;
  font-size: 16px;
}

.no-data {
  text-align: center;
  color: #8c8c8c;
  padding: 40px 0;
}

:deep(.el-dialog) {
  background-color: #282b30;
}

:deep(.el-dialog__title) {
  color: #fff;
}

:deep(.el-dialog__body) {
  color: #fff;
}

:deep(.el-divider) {
  background-color: #303030;
}

:deep(.el-divider__text) {
  color: #8c8c8c;
  background-color: #282b30;
}

:deep(.el-table) {
  background-color: transparent !important;
}

:deep(.el-table th) {
  background-color: #1a1c1e !important;
  border-bottom: 1px solid #303030;
  color: #8c8c8c;
}

:deep(.el-table td) {
  background-color: transparent !important;
  border-bottom: 1px solid #303030;
  color: #fff;
}

:deep(.el-table--enable-row-hover .el-table__body tr:hover > td) {
  background-color: #1a1c1e !important;
}

.filter-buttons {
  display: flex;
  gap: 12px;
}

.filter-section {
  display: flex;
  gap: 16px;
  align-items: center;
}

.section-header {
  margin-bottom: 20px;
}

.section-header h3 {
  margin-bottom: 16px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 20px;
  padding: 0 20px;
  gap: 16px;
}

.total-text {
  color: #fff;
  font-size: 14px;
}

.page-control {
  display: flex;
  align-items: center;
  gap: 16px;
}

.page-size-select {
  width: 110px;
}

.page-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

.current-page {
  color: #409EFF;
  font-size: 14px;
  min-width: 24px;
  text-align: center;
}

:deep(.el-select .el-input__wrapper) {
  background-color: transparent;
  box-shadow: 0 0 0 1px #606266;
}

:deep(.el-select .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #409EFF;
}

:deep(.el-select .el-input__inner) {
  color: #fff;
}

:deep(.el-select-dropdown) {
  background-color: #282b30;
  border: 1px solid #303030;
}

:deep(.el-select-dropdown__item) {
  color: #fff;
}

:deep(.el-select-dropdown__item.hover),
:deep(.el-select-dropdown__item:hover) {
  background-color: rgba(64, 158, 255, 0.1);
}

:deep(.el-select-dropdown__item.selected) {
  background-color: #409EFF;
  color: #fff;
}

:deep(.el-button) {
  border-radius: 4px;
  padding: 8px 20px;
  font-size: 14px;
  border: 1px solid transparent;
}

:deep(.el-button--primary) {
  background-color: #409EFF;
  border-color: #409EFF;
  color: #fff;
}

:deep(.el-button--primary:hover) {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

:deep(.el-button--default) {
  background-color: transparent;
  border-color: #606266;
  color: #606266;
}

:deep(.el-button--default:hover) {
  color: #409EFF;
  border-color: #409EFF;
  background-color: rgba(64, 158, 255, 0.1);
}
</style>