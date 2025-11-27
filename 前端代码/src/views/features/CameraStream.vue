<template>
  <div class="feature-page dark-theme">
    <CommonHeader :currentTime="currentTime" :theme="theme" @toggleTheme="toggleTheme" />
    <div class="main-content">
      <CommonSidebar />
      <div class="main">
        <CommonBreadcrumb :paths="breadcrumbPaths" />
        <div class="content-section">
          <div class="card">
            <h2>摄像头实时检测画面</h2>
            <!-- <div style="color:red; margin-bottom:8px;">
              调试: recording={{ recording }}, watching={{ watching }}, selectedModel={{ selectedModel }}, selectedCamera={{ selectedCamera }}
            </div> -->
            <div class="operation-card">
              <div class="operation-row">
                <el-select v-model="selectedCamera" placeholder="选择摄像头" class="op-item">
                  <el-option v-for="cam in cameraList" :key="cam" :label="cam" :value="cam" />
                  <el-option v-if="cameraList.length === 0" disabled label="无可用摄像头" value="" />
                </el-select>
                <el-select v-model="selectedModel" placeholder="选择检测模型" @change="onModelChange" class="op-item">
                  <el-option v-for="model in modelList" :key="model" :label="model" :value="model" />
                </el-select>
                <span class="op-label">跳帧数：</span>
                <el-input-number v-model="frameInterval" :min="1" :step="1" style="margin-right:8px;flex:1 1 80px;" />
                <span class="op-desc">（每隔N帧检测，1为每帧检测）</span>
              </div>
              <div class="button-row">
                <el-button type="primary" @click="fetchModelList" :loading="loadingModels" class="op-btn">刷新模型列表</el-button>
                <el-button type="info" @click="watchStream" :disabled="watching || !selectedModel || !selectedCamera" class="op-btn">只看流</el-button>
                <el-button type="warning" @click="exitWatchStream" :disabled="!watching" class="op-btn">退出只看流</el-button>
                <el-button type="success" @click="startRecord" :disabled="recording || !selectedModel || !selectedCamera || watching" class="op-btn">开始录制</el-button>
                <el-button type="danger" @click="stopRecord" :disabled="!recording" class="op-btn">结束录制</el-button>
              </div>
            </div>
            <div class="stream-container">
              <img v-if="streamUrl && (watching || recording)" :src="streamUrl" alt="摄像头流" style="max-width: 100%; border-radius: 10px; background: #000; min-height: 320px;" />
              <div v-else class="empty-stream">请选择摄像头和模型并点击“只看流”或“开始录制”以展示实时画面</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { yoloApi } from '@/api/yolo';
import CommonHeader from '@/components/CommonHeader.vue';
import CommonSidebar from '@/components/CommonSidebar.vue';
import CommonBreadcrumb from '@/components/CommonBreadcrumb.vue';

const currentTime = ref(new Date().toLocaleString());
const theme = ref('dark');
const breadcrumbPaths = [
  { name: '首页', path: '/' },
  { name: '摄像头实时流', path: '' }
];

const cameraList = ref<string[]>([]);
const selectedCamera = ref('');
const modelList = ref<string[]>([]);
const selectedModel = ref('');
const streamUrl = ref('');
const loadingModels = ref(false);
const detecting = ref(false);
const watching = ref(false);
const recording = ref(false);
const frameInterval = ref(1); // 跳帧数，默认1

const fetchCameraList = async () => {
  try {
    const res = await yoloApi.getCameraList();
    // 兼容直接数组或对象格式
    cameraList.value = Array.isArray(res) ? res : (res.data || []);
    console.log('cameraList', cameraList.value);
    if (cameraList.value.length && !selectedCamera.value) {
      selectedCamera.value = cameraList.value[0];
    }
  } catch (e) {
    cameraList.value = [];
    console.error('获取摄像头列表失败', e);
  }
};

const fetchModelList = async () => {
  loadingModels.value = true;
  try {
    modelList.value = await yoloApi.getModelList();
    if (modelList.value.length && !selectedModel.value) {
      selectedModel.value = modelList.value[0];
      onModelChange();
    }
  } finally {
    loadingModels.value = false;
  }
};

const watchStream = async () => {
  if (!selectedModel.value || !selectedCamera.value) return;
  console.log('当前跳帧数:', frameInterval.value); // 调试用
  streamUrl.value = await yoloApi.watchStream(selectedModel.value, selectedCamera.value, frameInterval.value);
  watching.value = true;
  recording.value = false;
  detecting.value = false;
};

const startRecord = async () => {
  if (!selectedModel.value || !selectedCamera.value) return;
  recording.value = true; // 立即允许停止
  watching.value = false;
  detecting.value = true;
  console.log('当前跳帧数:', frameInterval.value); // 调试用
  // 调用接口不阻塞按钮状态
  const url = await yoloApi.recordStream(selectedModel.value, selectedCamera.value, frameInterval.value);
  console.log('recordStream 返回的 url:', url);
  streamUrl.value = url;
};

const stopRecord = async () => {
  try {
    await yoloApi.stopRecordStream(selectedCamera.value);
  } catch (e) {
    // 可选：ElMessage.error(e.message || '停止录制失败');
  } finally {
    recording.value = false;
    detecting.value = false;
    watching.value = false;
    streamUrl.value = '';
    selectedModel.value = selectedModel.value;
    selectedCamera.value = selectedCamera.value;
    console.log('stopRecord done', recording.value, watching.value, selectedModel.value, selectedCamera.value);
  }
};

const onModelChange = () => {
  streamUrl.value = '';
  detecting.value = false;
  watching.value = false;
  recording.value = false;
};

const exitWatchStream = async () => {
  if (selectedCamera.value !== undefined && selectedCamera.value !== null && selectedCamera.value !== '') {
    try {
      await yoloApi.stopCameraStream(selectedCamera.value);
    } catch (e) {
      // 可选：ElMessage.error(e.message || '关闭流失败');
    }
  }
  streamUrl.value = '';
  watching.value = false;
};

onMounted(() => {
  fetchCameraList();
  fetchModelList();
});
// 页面卸载时自动释放流资源
onUnmounted(() => {
  if (selectedCamera.value !== undefined && selectedCamera.value !== null && selectedCamera.value !== '') {
    yoloApi.stopCameraStream(selectedCamera.value);
  }
});
setInterval(() => {
  currentTime.value = new Date().toLocaleString();
}, 1000);

const toggleTheme = () => {
  theme.value = theme.value === 'dark' ? 'light' : 'dark';
};
</script>

<style scoped>
.feature-page {
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
  overflow-y: auto;
}
.content-section {
  max-width: 1200px;
  margin: 0 auto;
}
.card {
  margin-bottom: 32px;
  background: transparent;
  border: 1.5px solid #3a3f55;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.06);
  padding: 32px 28px 28px 28px;
  transition: box-shadow 0.2s;
  max-width: 1200px;
}
.card:hover {
  box-shadow: 0 8px 32px rgba(0,0,0,0.12);
  border-color: #409eff;
}
.stream-container {
  min-height: 320px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #181a1b;
  border-radius: 10px;
  border: 1px solid #333;
  margin-top: 12px;
}
.empty-stream {
  color: #888;
  font-size: 18px;
  padding: 60px 0;
}
.operation-card {
  background: rgba(255,255,255,0.04);
  border-radius: 14px;
  box-shadow: 0 2px 16px rgba(0,0,0,0.10);
  padding: 22px 18px 18px 18px;
  margin-bottom: 24px;
  border: 1.5px solid #2a2d3e;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}
.operation-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: center;
  margin-bottom: 8px;
}
.button-row {
  display: flex;
  flex-wrap: wrap;
  gap: 18px;
  justify-content: flex-end;
  margin-top: 10px;
}
.op-item {
  min-width: 120px;
  flex: 1 1 180px;
  max-width: 240px;
  height: 38px;
}
.op-item.short {
  max-width: 90px;
  min-width: 60px;
  flex: 0 1 80px;
}
.op-label, .op-desc {
  font-size: 13px;
  color: #aaa;
  white-space: nowrap;
}
.op-desc {
  margin-left: 4px;
  flex: 1 1 120px;
  text-align: right;
}
.op-btn {
  flex: 1 1 140px;
  min-width: 110px;
  max-width: 200px;
  height: 40px;
  border-radius: 8px;
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  transition: box-shadow 0.2s, background 0.2s;
}
.op-btn.primary {
  background: #409eff;
  color: #fff;
  border: none;
}
.op-btn.success {
  background: #52c41a;
  color: #fff;
  border: none;
}
.op-btn.danger {
  background: #ff7875;
  color: #fff;
  border: none;
}
.op-btn.warning {
  background: #faad14;
  color: #fff;
  border: none;
}
.op-btn:hover, .op-btn:focus {
  box-shadow: 0 4px 16px rgba(64,158,255,0.15);
  filter: brightness(1.08);
}
@media (max-width: 900px) {
  .operation-card { padding: 14px 6vw 12px 6vw; }
  .operation-row, .button-row { gap: 10px; }
  .op-item, .op-btn { max-width: 100%; min-width: 0; flex: 1 1 100%; }
  .op-desc { text-align: left; }
}
@media (max-width: 600px) {
  .operation-card { padding: 8px 2vw 8px 2vw; }
  .operation-row, .button-row { flex-direction: column; align-items: stretch; }
  .op-item, .op-btn { height: 36px; }
}
</style> 