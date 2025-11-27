<!--
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-04-18 13:49:49
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-05-03 17:21:29
 * @FilePath: \front\src\views\StatusMonitoring.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
  <div class="status-monitoring dark-theme">
    <CommonHeader :currentTime="currentTime" :theme="theme" @toggleTheme="toggleTheme" />
    <div class="main-content">
      <CommonSidebar :isCollapse="isCollapse" />
      <div class="main">
        <CommonBreadcrumb :paths="[{name: '首页', path: '/'}, {name: '状态监控', path: '/status-monitoring'}]" />
        
        <div class="content-section">
          <div class="page-header">
            <h2>设备状态</h2>
            <div class="header-actions">
              <el-select v-model="formWorkshop" placeholder="车间" clearable>
                <el-option label="全部" value="" />
                <el-option label="车间A" value="车间A" />
                <el-option label="车间B" value="车间B" />
                <el-option label="车间C" value="车间C" />
              </el-select>
              <el-select v-model="formDeviceType" placeholder="设备类型" clearable>
                <el-option label="全部" value="" />
                <el-option label="水泵" value="水泵" />
                <el-option label="联轴器" value="联轴器" />
                <el-option label="EDI装置" value="EDI装置" />
              </el-select>
              <el-select v-model="formStatusFilter" placeholder="状态筛选" clearable>
                <el-option
                  v-for="option in statusOptions"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                />
              </el-select>
              <el-input
                v-model="formSearchKeyword"
                placeholder="搜索测点名称"
                clearable
                class="search-input"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <el-button type="primary" @click="handleQuery">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </div>
          </div>

          <div class="device-grid">
            <DeviceStatusCard
              v-for="device in deviceList"
              :key="device.mpId"
              :device="{
                mpId: device.mpId,
                name: `${device.deviceName}-${device.measuringPointName}`,
                description: `${device.workshopName || device.workshop}`,
                lastUpdate: new Date().toLocaleString(),
                status: device.measuringPointStatus === 0 ? 'normal' : 'alarm',
                imageUrl: device.base64Image ? (device.base64Image.startsWith('data:image') 
                  ? device.base64Image 
                  : `data:image/jpeg;base64,${device.base64Image}`)
                  : '/images/devices/default.jpg'
              }"
            />
          </div>

          <div class="pagination">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="total"
              :page-sizes="[12, 24, 36, 48]"
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

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { Search } from '@element-plus/icons-vue';
import DeviceStatusCard from '@/components/DeviceStatusCard.vue';
import CommonHeader from '@/components/CommonHeader.vue';
import CommonSidebar from '@/components/CommonSidebar.vue';
import CommonBreadcrumb from '@/components/CommonBreadcrumb.vue';
import { mpApi } from '@/api';
import { ElMessage } from 'element-plus';

const currentTime = ref(new Date().toLocaleString());
const theme = ref('dark');
const isCollapse = ref(false);
const deviceList = ref<MeasurementPointItem[]>([]);
const formWorkshop = ref('');
const formDeviceType = ref('');
const formStatusFilter = ref(-1);
const formSearchKeyword = ref('');
const currentPage = ref(1);
const pageSize = ref(12);
const total = ref(0);

// 修改状态选择器选项（0正常，1异常）
const statusOptions = [
  { label: '全部', value: -1 },
  { label: '正常', value: 0 },
  { label: '异常', value: 1 }
];

// 加载测点列表
const loadMeasurementPoints = async () => {
  try {
    const params = {
      pageNo: currentPage.value,
      pageSize: pageSize.value,
      workshop: formWorkshop.value || undefined,
      deviceName: formDeviceType.value || undefined,
      measuringPointStatus: formStatusFilter.value === -1 ? undefined : formStatusFilter.value,
      measuringPointName: formSearchKeyword.value || undefined,
      workshopName: formWorkshop.value || undefined
    };

    const res = await mpApi.loadList(params);
    if (res.code === 200) {
      console.log('API Response:', res.data);
      deviceList.value = res.data.list.map(item => {
        console.log('Processing device item:', item);
        const base64Image = item.base64Image 
          ? (item.base64Image.startsWith('data:image') 
            ? item.base64Image 
            : `data:image/jpeg;base64,${item.base64Image}`)
          : '';
          
        return {
          ...item,
          status: item.measuringPointStatus === 0 ? 'normal' : 'alarm',
          statusText: item.measuringPointStatus === 0 ? '正常' : '异常',
          image: base64Image
        };
      });
      console.log('Processed device list:', deviceList.value);
      total.value = res.data.pageTotal;
      currentPage.value = res.data.pageNo;
      pageSize.value = res.data.pageSize;
    } else {
      ElMessage.error(res.info || '获取数据失败');
    }
  } catch (error) {
    console.error('获取测点列表失败:', error);
    ElMessage.error('获取数据失败');
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

const handleSizeChange = (val: number) => {
  pageSize.value = val;
  loadMeasurementPoints();
};

const handleCurrentChange = (val: number) => {
  currentPage.value = val;
  loadMeasurementPoints();
};

const handleQuery = () => {
  currentPage.value = 1;
  loadMeasurementPoints();
};

const handleReset = () => {
  formWorkshop.value = '';
  formDeviceType.value = '';
  formStatusFilter.value = -1;
  formSearchKeyword.value = '';
  handleQuery();
};

onMounted(() => {
  loadMeasurementPoints();
});
</script>

<style scoped>
.status-monitoring {
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
  margin-bottom: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 500;
  color: #fff;
}

.header-actions {
  display: flex;
  gap: 16px;
}

.search-input {
  width: 240px;
}

.device-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
  margin-bottom: 24px;
  padding: 12px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}

:deep(.el-select) {
  width: 160px;
}

:deep(.el-pagination) {
  --el-pagination-bg-color: transparent;
  --el-pagination-text-color: #8c8c8c;
  --el-pagination-button-color: #fff;
  --el-pagination-button-bg-color: #1a1c1e;
  --el-pagination-button-disabled-color: #606266;
  --el-pagination-button-disabled-bg-color: #282b30;
  --el-pagination-hover-color: var(--el-color-primary);
}

:deep(.el-card) {
  background-color: #282b30;
  border: none;
  transition: transform 0.3s ease;
}

:deep(.el-card:hover) {
  transform: translateY(-4px);
}

:deep(.el-card__body) {
  padding: 0;
}
</style>