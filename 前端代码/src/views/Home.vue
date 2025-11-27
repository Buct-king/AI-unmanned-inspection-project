<!--
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-04-18 13:39:32
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-07-14 01:34:22
 * @FilePath: \front\src\views\Home.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
  <div class="home-container">
    <el-loading 
      v-if="loading" 
      :fullscreen="true" 
      text="加载中..."
    />
    <div class="header">
      <div class="logo">无人巡检系统</div>
      <div class="nav-tabs">
        <router-link 
          v-for="(item, index) in navItems" 
          :key="index"
          :to="item.path"
          class="nav-item"
          :class="{ active: currentPath === item.path }"
        >
          {{ item.name }}
        </router-link>
      </div>
      <div class="header-right">
        <span class="time">{{ currentTime }}</span>
        <div class="user-info" v-if="!userStore.isLoggedIn()" @click="goToLogin">
          <el-icon><User /></el-icon>
          <span>未登录</span>
        </div>
        <el-dropdown v-else trigger="click" @command="handleCommand">
          <span class="user-info">
            <el-icon><User /></el-icon>
            {{ userInfo.name }}
            <el-icon><CaretBottom /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon><User /></el-icon>个人信息
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <el-icon style="color: #ff4d4f;"><SwitchButton /></el-icon>退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <el-row :gutter="20" class="dashboard-row" align="stretch" style="height: calc(100vh - 60px);">
      <el-col :xs="24" :sm="24" :md="6" :lg="6" class="left-panel">
        <div class="chart-card">
          <div class="card-header">车间设备统计</div>
          <div ref="deviceTypeChart" class="chart-container"></div>
        </div>
        <div class="chart-card">
          <div class="card-header">设备类型占比</div>
          <div ref="deviceTypePieChart" class="chart-container"></div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="12" class="center-panel">
        <div class="status-overview">
          <div class="status-item" :class="'status-' + item.type" v-for="item in statusItems" :key="item.type">
            <div class="status-value">{{ item.value }}%</div>
            <div class="status-label">{{ item.label }}</div>
            <div class="status-count">{{ item.count }}台</div>
          </div>
        </div>
        <div class="device-carousel">
          <div class="carousel-header">
            <span class="title">设备运行状态</span>
          </div>
          <div class="carousel-content">
            <div class="device-info" v-if="currentDevice">
              <div class="device-image">
                <el-image 
                  :src="currentDevice.image" 
                  :alt="currentDevice.model"
                  fit="contain"
                  :preview-src-list="currentDevice.image ? [currentDevice.image] : []"
                  :initial-index="0"
                  :loading="true"
                >
                  <template #error>
                    <div class="image-error">
                      <el-icon><Picture /></el-icon>
                      <span>加载失败</span>
                    </div>
                  </template>
                  <template #placeholder>
                    <div class="image-loading">
                      <el-icon class="is-loading"><Loading /></el-icon>
                      <span>加载中...</span>
                    </div>
                  </template>
                </el-image>
              </div>
              <div class="device-meta">
                <div class="device-model-status">
                  <span class="device-model">{{ currentDevice.model }}</span>
                  <span class="status-badge" :class="currentDevice.status">
                    {{ getStatusText(currentDevice.status) }}
                  </span>
                </div>
                <div class="device-workshop">
                  {{ currentDevice.workshop }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :md="6" :lg="6" class="right-panel">
        <div class="chart-card">
          <div class="card-header">报警统计列表</div>
          <div ref="alarmStatChart" class="chart-container"></div>
        </div>
        <div class="chart-card">
          <div class="card-header">报警对比统计</div>
          <div ref="alarmCompareChart" class="chart-container"></div>
        </div>
        <div class="chart-card">
          <div class="card-header">处理时间统计</div>
          <div ref="processTimeChart" class="chart-container"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, computed, watch, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import * as echarts from 'echarts';
import 'echarts-gl';
import { User, CaretBottom, SwitchButton, Picture, Loading } from '@element-plus/icons-vue';
import { ElMessageBox, ElMessage, ElDropdown, ElDropdownMenu, ElDropdownItem, ElFormItem } from 'element-plus';
import { accountApi, alarmApi, deviceApi, workshopApi, mpApi } from '../api/index';
import { useUserStore } from '@/stores/user';

// Register map component
echarts.registerMap('china', {
  type: 'FeatureCollection',
  features: []
});

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

// 当前路径
const currentPath = computed(() => route.path);
const currentTime = ref(new Date().toLocaleString());

// 导航项配置
const navItems = [
  { name: '工作台', path: '/workspace' },
  { name: '状态监测', path: '/status-monitoring' },
  { name: '报警管理', path: '/alarm-management' },
  // { name: '开停机记录', path: '/shutdown-record' },
  { name: '系统管理', path: '/system-management' }, 
  { name: '实时摄像', path: '/camera-stream' },  // 实时摄像
  { name: '智能检测', path: '/smart-detect' },  // 智能检测
  { name: '智能训练', path: '/smart-training' },  // 智能训练  

  // { name: '功能详情', path: '/features' }
];

// 状态概览数据
const statusItems = ref([
  { type: 'normal', label: '运行中', value: 0, count: 0 },
  { type: 'warning', label: '报警', value: 0, count: 0 },
  { type: 'pending', label: '未处理', value: 0, count: 0 },
  { type: 'success', label: '已处理', value: 0, count: 0 }
]);

const showArrows = ref(false);

// 设备列表数据（加id字段）
const deviceList = ref([]);
const currentDeviceIndex = ref(0);
const currentDevice = computed(() => deviceList.value[currentDeviceIndex.value]);

// 轮播定时器
let carouselTimer;

// 获取测点列表
const fetchMeasurementPoints = async () => {
  try {
    const res = await mpApi.loadList({
      pageNo: 1,
      pageSize: 10,  // 获取10条数据
      measuringPointStatus: undefined  // 不过滤状态
    });
    
    if (res.code === 200) {
      deviceList.value = res.data.list.map(item => ({
        mpId: item.mpId,  // 保存测点ID
        model: `${item.deviceName}-${item.measuringPointName}`,
        image: '',  // 图片地址先置空
        status: item.measuringPointStatus === 0 ? 'normal' : 'warning',
        workshop: item.workshopName || item.workshop,
        description: item.description,
        imageLoaded: false  // 标记图片是否已加载
      }));
      
      // console.log('测点列表:', deviceList.value); // 添加日志
      
      // 初始加载第一个测点的图片
      if (deviceList.value.length > 0) {
        await loadPointImage(0);
      }
    }
  } catch (error) {
    // console.error('获取测点列表失败:', error);
    ElMessage.error('获取测点列表失败');
  }
};

// 加载指定测点的图片
const loadPointImage = async (index) => {
  const point = deviceList.value[index];
  if (!point || point.imageLoaded) return;  // 如果已加载过，直接返回

  // console.log('加载测点图片:', point);

  try {
    const imgRes = await mpApi.loadImage({ 
      mpId: point.mpId  // 使用测点ID请求图片
    });
    if (imgRes.code === 200 && imgRes.data) {
      // 确保base64数据格式正确
      point.image = imgRes.data.startsWith('data:image') 
        ? imgRes.data 
        : `data:image/jpeg;base64,${imgRes.data}`;
    } else {
      point.image = 'https://example.com/device1.jpg';  // 设置默认图片
    }
    point.imageLoaded = true;  // 标记为已加载
    // console.log(point.image)
  } catch (error) {
    // console.error(`获取测点${point.mpId}图片失败:`, error);
    point.image = 'https://example.com/device1.jpg';
    point.imageLoaded = true;
  }
};

// 自动切换设备
const autoSwitchDevice = async () => {
  if (deviceList.value.length > 0) {
    const nextIndex = (currentDeviceIndex.value + 1) % deviceList.value.length;
    // console.log('切换到下一个测点:', nextIndex, deviceList.value[nextIndex]); // 添加日志
    // 预加载下一个测点的图片
    await loadPointImage(nextIndex);
    currentDeviceIndex.value = nextIndex;
  }
};

// 重置轮播定时器
const resetCarouselTimer = () => {
  if (carouselTimer) {
    clearInterval(carouselTimer);
  }
  carouselTimer = setInterval(async () => {
    await autoSwitchDevice();
  }, 5000);
};

// 监听当前设备索引变化，提前加载下一个测点的图片
watch(currentDeviceIndex, async (newIndex) => {
  const nextIndex = (newIndex + 1) % deviceList.value.length;
  // 预加载下一个测点的图片
  await loadPointImage(nextIndex);
});

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    normal: '正常运行',
    warning: '异常运行',
    stopped: '已停机',
    offline: '离线'
  };
  return statusMap[status] || '未知状态';
};

// 图表引用
const deviceTypeChart = ref(null);
const deviceStatusChart = ref(null);
const deviceTypePieChart = ref(null);
const chinaMapChart = ref(null);
const alarmStatChart = ref(null);
const processTimeChart = ref(null);
const alarmCompareChart = ref(null);

// 定时器引用
let timer;

// 用户信息
const userInfo = computed(() => {
  return {
    name: userStore.currentUser?.name || '未登录',
    role: userStore.currentUser?.roleType || 0
  };
});

// 监听路由变化
watch(() => route.path, (newPath) => {
  // console.log('路由变化:', newPath);
});

// 监听用户信息变化
watch(
  () => userStore.currentUser,
  (newUser) => {
    if (newUser) {
      // 强制更新组件
      currentTime.value = new Date().toLocaleString();
    }
  },
  { deep: true }
);

// 跳转到登录页面
const goToLogin = () => {
  router.push('/login');
};

// 获取状态统计数据
const fetchStatusData = async () => {
  try {
    // 获取报警处理状态统计
    const alarmRes = await alarmApi.getStatusCount();
    if (alarmRes.code === 200) {
      const { processedCount, unprocessedCount } = alarmRes.data;
      const totalAlarms = processedCount + unprocessedCount;
      
      // 更新已处理和未处理的数据
      statusItems.value[2].count = unprocessedCount;
      statusItems.value[2].value = totalAlarms ? Math.round((unprocessedCount / totalAlarms) * 100) : 0;
      
      statusItems.value[3].count = processedCount;
      statusItems.value[3].value = totalAlarms ? Math.round((processedCount / totalAlarms) * 100) : 0;
    }

    // 获取设备状态统计
    const deviceRes = await deviceApi.countStatus();
    if (deviceRes.code === 200) {
      const { runningCount, alarmCount, runningPercent, alarmPercent } = deviceRes.data;
      
      // 更新运行中和报警的数据
      statusItems.value[0].count = runningCount;
      statusItems.value[0].value = runningPercent*100;
      
      statusItems.value[1].count = alarmCount;
      statusItems.value[1].value = alarmPercent*100;
    }
  } catch (error) {
    // console.error('获取状态统计数据失败:', error);
    ElMessage.error('获取状态统计数据失败');
  }
};

// 获取设备类型统计数据
const fetchWorkshopData = async () => {
  try {
    const res = await workshopApi.countDevice();
    if (res.code === 200) {
      const data = res.data;
      // 转换数据格式
      const xAxisData = Object.keys(data);
      const seriesData = Object.values(data);

      // 更新图表
      if (deviceTypeChart.value) {
        const chart = echarts.init(deviceTypeChart.value);
        chart.setOption({
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            },
            formatter: '{b}: {c}台'
          },
          grid: {
            top: '10%',
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            data: xAxisData,
            axisLine: {
              lineStyle: { color: '#8c8c8c' }
            },
            axisLabel: {
              interval: 0,
              rotate: 30
            }
          },
          yAxis: {
            type: 'value',
            axisLine: {
              lineStyle: { color: '#8c8c8c' }
            },
            splitLine: {
              lineStyle: { color: 'rgba(140, 140, 140, 0.2)' }
            }
          },
          series: [{
            data: seriesData,
            type: 'bar',
            barWidth: '30%',
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#83bff6' },
                { offset: 0.5, color: '#188df0' },
                { offset: 1, color: '#188df0' }
              ])
            }
          }]
        });
      }
    }
  } catch (error) {
    // console.error('获取设备类型统计数据失败:', error);
    ElMessage.error('获取设备类型统计数据失败');
  }
};

// 获取设备类型统计数据（饼图）
const fetchDeviceTypeData = async () => {
  try {
    const res = await deviceApi.countType();
    if (res.code === 200) {
      const data = res.data;
      // 转换数据格式为饼图所需格式
      const pieData = Object.entries(data).map(([name, value]) => ({
        name,
        value
      }));

      // 更新饼图
      if (deviceTypePieChart.value) {
        const chart = echarts.init(deviceTypePieChart.value);
        chart.setOption({
          tooltip: {
            trigger: 'item'
          },
          legend: {
            orient: 'vertical',
            right: '10%',
            top: 'center',
            textStyle: { color: '#8c8c8c' }
          },
          series: [{
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['40%', '50%'],
            data: pieData,
            itemStyle: {
              borderColor: '#1a1c1e',
              borderWidth: 2
            },
            label: {
              show: false
            }
          }]
        });
      }
    }
  } catch (error) {
    // console.error('获取设备类型统计数据失败:', error);
    ElMessage.error('获取设备类型统计数据失败');
  }
};

// 获取报警类型统计数据
const fetchAlarmTypeData = async () => {
  try {
    const res = await alarmApi.countType();
    if (res.code === 200) {
      const data = res.data;
      // 转换数据格式
      const xAxisData = Object.keys(data);
      const seriesData = Object.values(data);

      // 更新报警统计图表
      if (alarmStatChart.value) {
        const chart = echarts.init(alarmStatChart.value);
        chart.setOption({
          grid: {
            top: '10%',
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
          },
          xAxis: {
            type: 'category',
            data: xAxisData,
            axisLine: {
              lineStyle: { color: '#8c8c8c' }
            },
            axisLabel: {
              interval: 0,
              rotate: 30,
              color: '#8c8c8c'
            }
          },
          yAxis: {
            type: 'value',
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
            data: seriesData,
            type: 'bar',
            barWidth: '40%',
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#ff4e50' },
                { offset: 1, color: '#ff7875' }
              ])
            }
          }]
        });
      }
    }
  } catch (error) {
    // console.error('获取报警类型统计数据失败:', error);
    ElMessage.error('获取报警类型统计数据失败');
  }
};

// 添加月度报警统计数据获取方法
const fetchAlarmMonthlyData = async () => {
  try {
    const res = await alarmApi.countAlarmTimeByMonth();
    if (res.code === 200) {
      const data = res.data;
      // 更新报警趋势图表
      if (alarmCompareChart.value) {
        const chart = echarts.init(alarmCompareChart.value);
        chart.setOption({
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
          },
          grid: {
            top: '10%',
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
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
            name: '月度报警数',
            type: 'line',
            smooth: true,
            data: Object.values(data),
            itemStyle: {
              color: '#ff4d4f'
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(255, 77, 79, 0.3)' },
                { offset: 1, color: 'rgba(255, 77, 79, 0.1)' }
              ])
            },
            emphasis: {
              focus: 'series'
            }
          }]
        });
      }
    }
  } catch (error) {
    // console.error('获取月度报警统计数据失败:', error);
    ElMessage.error('获取月度报警统计数据失败');
  }
};

// 添加处理时间统计数据获取方法
const fetchProcessTimeData = async () => {
  try {
    const res = await alarmApi.countProcessedTimeByMonth();
    if (res.code === 200) {
      const data = res.data;
      // 更新处理时间统计图表
      if (processTimeChart.value) {
        const chart = echarts.init(processTimeChart.value);
        chart.setOption({
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
          },
          grid: {
            top: '10%',
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
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
            name: '平均处理时间(分钟)',
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
            name: '处理时间',
            type: 'bar',
            data: Object.values(data),
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#409EFF' },
                { offset: 1, color: '#36CFC9' }
              ])
            },
            barWidth: '40%'
          }]
        });
      }
    }
  } catch (error) {
    // console.error('获取处理时间统计数据失败:', error);
    ElMessage.error('获取处理时间统计数据失败');
  }
};

// 修改刷新间隔为5分钟，减少请求频率
const REFRESH_INTERVAL = 300000; // 5分钟刷新一次

// 添加加载状态和刷新计数
const loading = ref(false);
const refreshCount = ref(0);

// 添加 refreshTimer 声明
let refreshTimer = null;

// 优化组件挂载逻辑
onMounted(async () => {
  try {
    // 初始化时间更新
    timer = setInterval(() => {
      currentTime.value = new Date().toLocaleString();
    }, 1000);

    // 分步骤初始化
    await initializeData();
    
    // 开始自动刷新
    startAutoRefresh();
    
    // 初始化轮播
    resetCarouselTimer();
  } catch (error) {
    console.error('组件初始化失败:', error);
    ElMessage.error('页面加载失败，请刷新重试');
  }
});

// 优化初始化逻辑
const initializeData = async () => {
  if (loading.value) return;
  
  loading.value = true;
  try {
    // 然后加载数据
    await loadData();
  } catch (error) {
    console.error('初始化失败:', error);
    throw error;
  } finally {
    loading.value = false;
  }
};

// 优化数据加载逻辑
const loadData = async () => {
  try {
    const promises = [
      fetchStatusData(),
      fetchWorkshopData(),
      fetchAlarmTypeData(),
      fetchDeviceTypeData(),
      fetchAlarmMonthlyData(),
      fetchProcessTimeData(),
      fetchMeasurementPoints() // 添加测点列表获取
    ];
    
    await Promise.all(promises);
    
    await nextTick();
    updateCharts();
  } catch (error) {
    console.error('加载数据失败:', error);
    throw error;
  }
};

// 添加更新图表的方法
const updateCharts = () => {
  if (!deviceTypeChart.value || !alarmStatChart.value) {
    return;
  }

  // 重新渲染图表...
  // 这里保持原有的图表更新逻辑
};

// 优化自动刷新逻辑
const startAutoRefresh = () => {
  stopAutoRefresh(); // 确保先停止之前的定时器
  
  refreshTimer = setInterval(() => {
    if (!document.hidden) {
      loadData().catch(error => {
        console.error('自动刷新失败:', error);
      });
    }
  }, REFRESH_INTERVAL);
};

// 添加停止自动刷新的方法
const stopAutoRefresh = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer);
    refreshTimer = null;
  }
};

// 优化组件卸载清理
onBeforeUnmount(() => {
  // 清理所有定时器
  if (timer) {
    clearInterval(timer);
    timer = null;
  }
  
  if (carouselTimer) {
    clearInterval(carouselTimer);
    carouselTimer = null;
  }
  
  stopAutoRefresh();
  
  // 销毁图表实例
  if (deviceTypeChart.value) {
    echarts.dispose(deviceTypeChart.value);
  }
  if (alarmStatChart.value) {
    echarts.dispose(alarmStatChart.value);
  }
  if (deviceTypePieChart.value) {
    echarts.dispose(deviceTypePieChart.value);
  }
  if (alarmCompareChart.value) {
    echarts.dispose(alarmCompareChart.value);
  }
  if (processTimeChart.value) {
    echarts.dispose(processTimeChart.value);
  }
});

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
        
        // 调用后端登出接口，添加userId参数
        const userId = userStore.currentUser?.userId;
        // 直接传递userId值，而不是包装成对象
        const res = await accountApi.logout(userId);
        
        // 先检查401状态
        if (res.code === 401) {
          ElMessage.warning(res.info || '登录已过期');
          // 清除用户状态
          userStore.clearUser();
          // 清除localStorage中的持久化数据
          localStorage.removeItem('user');
          // 跳转到登录页
          router.push('/login');
          return;
        }
        
        // 处理其他错误状态
        if (res.code !== 200) {
          throw new Error(res.info || '退出失败');
        }
        
        // 正常退出流程
        userStore.clearUser();
        localStorage.removeItem('user');
        router.push('/login');
        ElMessage.success('已退出登录');
      } catch (error) {
        if (error !== 'cancel') {
          console.error('退出登录错误:', error);
          ElMessage.error(error.message || '退出失败，请重试');
        }
      }
      break;
  }
};

</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background-color: #1a1c1e;
  color: #fff;
}

.header {
  height: 60px;
  background-color: #282b30;
  display: flex;
  align-items: center;
  padding: 0 20px;
  justify-content: space-between;
  border-bottom: 1px solid #303030;
}

.logo {
  font-size: 20px;
  font-weight: 500;
}

.nav-tabs {
  display: flex;
  gap: 4px;
}

.nav-item {
  padding: 0 20px;
  height: 60px;
  line-height: 60px;
  color: #8c8c8c;
  text-decoration: none;
  position: relative;
}

.nav-item.active {
  color: #fff;
  background-color: #1a1c1e;
}

.nav-item:hover {
  color: #fff;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.time {
  color: #8c8c8c;
  font-size: 14px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #fff;
  cursor: pointer;
  padding: 0 8px;
  height: 32px;
  border-radius: 4px;
  transition: all 0.3s;
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.1);
}

.dashboard-row {
  width: 100%;
  min-width: 0;
  height: auto;
  align-items: stretch;
  overflow-x: hidden;
}

.left-panel, .center-panel, .right-panel {
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-width: 0;
  width: 100%;
  height: 100%;
}

.chart-card, .device-carousel {
  background-color: #282b30;
  border-radius: 8px;
  padding: clamp(12px, 2vw, 32px);
  flex: 1 1 0;
  min-width: 0;
  width: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  justify-content: center;
  max-width: 100%;
  overflow: auto;
}

.status-overview {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 2vw;
  margin-bottom: 20px;
}

@media (max-width: 1200px) {
  .status-overview {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 900px) {
  .dashboard-row {
    flex-direction: column !important;
    gap: 12px;
    padding: 0 2vw;
    height: auto;
  }
  .left-panel, .center-panel, .right-panel {
    width: 100% !important;
    min-width: 0 !important;
    gap: 12px;
    height: auto !important;
  }
  .chart-card, .device-carousel {
    padding: 12px 6px;
    height: auto !important;
    max-width: 100vw;
    flex: unset !important;
  }
  .status-overview {
    grid-template-columns: 1fr;
  }
}
@media (max-width: 800px) {
  .status-overview {
    grid-template-columns: 1fr;
  }
  .device-image {
    height: 180px;
  }
  .chart-card, .device-carousel {
    padding: 3vw 2vw;
  }
}
@media (max-width: 600px) {
  .dashboard-row {
    flex-direction: column;
    gap: 12px;
    padding: 0 2vw;
    height: auto;
  }
  .left-panel, .center-panel, .right-panel {
    width: 100%;
    min-width: 0;
    gap: 12px;
    height: auto;
  }
  .chart-card, .device-carousel {
    padding: 12px 6px;
    height: auto;
  }
}

.card-header {
  color: #fff;
  font-size: 16px;
  margin-bottom: 20px;
}

.chart-container {
  height: calc(100% - 40px);
}

.status-item {
  background-color: #282b30;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  position: relative;
}

.status-value {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 8px;
}

.status-label {
  color: #8c8c8c;
  font-size: 14px;
  margin-bottom: 4px;
}

.status-count {
  font-size: 16px;
}

.status-normal .status-value { color: #52c41a; }
.status-warning .status-value { color: #faad14; }
.status-pending .status-value { color: #ff4d4f; }
.status-success .status-value { color: #409eff; }

.status-badge.normal { background-color: #52c41a; }
.status-badge.warning { background-color: #faad14; }
.status-badge.pending { background-color: #ff4d4f; }
.status-badge.success { background-color: #409eff; }

.device-carousel {
  flex: 1;
  background-color: #282b30;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.carousel-header {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin-bottom: 20px;
}

.title {
  font-size: 16px;
  color: #fff;
}

.carousel-content {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.device-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 32px;
  width: 100%;
  height: 100%;
  background: none;
}

.device-image {
  width: 90%;
  height: 340px;
  background-color: #1a1c1e;
  border-radius: 12px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
}

.device-image :deep(.el-image) {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.device-image :deep(.el-image img) {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.device-meta {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.device-model-status {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
  justify-content: center;
}

.device-model {
  font-size: 24px;
  color: #fff;
  font-weight: bold;
}

.status-badge {
  padding: 4px 14px;
  border-radius: 4px;
  font-size: 14px;
  color: #fff;
}

.arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(40,43,48,0.3);
  border: none;
  color: #fff;
  font-size: 32px;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  cursor: pointer;
  z-index: 2;
  transition: background 0.2s, color 0.2s, opacity 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  pointer-events: none;
  filter: blur(1px);
}

.device-carousel:hover .arrow {
  opacity: 1;
  pointer-events: auto;
  filter: blur(0);
}

.arrow.left { left: 16px; }
.arrow.right { right: 16px; }
.arrow:hover {
  background: #ff4e50;
  color: #fff;
  filter: none;
}

:deep(.el-dropdown-menu) {
  background-color: #282b30 !important;
  border: 1px solid #303030 !important;
}

:deep(.el-dropdown-menu__item) {
  color: #fff !important;
  justify-content: flex-start !important;
}

:deep(.el-dropdown-menu__item:not(.is-disabled):hover) {
  background-color: rgba(64, 158, 255, 0.1) !important;
  color: #409EFF !important;
}

:deep(.el-dropdown-menu__item.is-disabled) {
  color: #606266 !important;
}

:deep(.el-dropdown-menu__item.el-dropdown-menu__item--divided) {
  border-top-color: #303030 !important;
}

:deep(.el-popper.is-light) {
  border: none !important;
}

:deep(.el-popper__arrow) {
  display: none !important;
}

/* 添加加载状态样式 */
:deep(.el-loading-mask) {
  background-color: rgba(0, 0, 0, 0.8);
}

:deep(.el-loading-text) {
  color: #fff;
}

:deep(.el-loading-spinner .path) {
  stroke: #409EFF;
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 14px;
  gap: 8px;
  height: 100%;
}

.image-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 14px;
  gap: 8px;
  height: 100%;
}

.image-loading .el-icon {
  font-size: 24px;
  animation: rotating 2s linear infinite;
}

@keyframes rotating {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.device-image :deep(.el-image__inner) {
  transition: all 0.3s ease;
}

.device-image :deep(.el-image__inner:hover) {
  transform: scale(1.02);
}
</style>