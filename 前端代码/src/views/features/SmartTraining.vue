<!--
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-07-10 23:00:00
 * @Description: 智能训练页，支持数据集上传、参数配置、训练、结果展示、模型管理等全流程
-->
<template>
  <div class="feature-page dark-theme">
    <CommonHeader :currentTime="currentTime" :theme="theme" @toggleTheme="toggleTheme" />
    <div class="main-content">
      <CommonSidebar />
      <div class="main">
        <CommonBreadcrumb :paths="breadcrumbPaths" />
        <div class="content-section">
          <!-- 功能介绍 -->
          <div class="card">
            <h2>{{ pageTitle }}</h2>
            <p class="description">{{ pageDescription }}</p>
          </div>
          <!-- 数据集上传与参数配置 -->
          <div class="card">
            <h2>训练数据集上传</h2>
            <el-upload
              class="upload-area"
              drag
              action="#"
              :auto-upload="false"
              :show-file-list="false"
              :on-change="handleDatasetChange"
              accept=".zip,.tar,.tar.gz"
              :disabled="uploadingDataset"
            >
              <el-icon class="upload-icon"><Upload /></el-icon>
              <div class="upload-text">
                <span>将数据集压缩包拖到此处，或<em>点击上传</em></span>
                <p>支持 zip、tar、tar.gz 格式</p>
              </div>
            </el-upload>
            <div v-if="selectedDatasetFile" class="file-info">
              <span class="file-name">{{ selectedDatasetFile.name }}</span>
              <span class="file-size">{{ formatFileSize(selectedDatasetFile.size) }}</span>
            </div>
            <div class="upload-row">
              <el-button 
                type="success" 
                @click="handleUploadDataset"
                :loading="uploadingDataset"
                :disabled="!selectedDatasetFile"
              >上传数据集</el-button>
              <el-button 
                v-if="selectedDatasetFile"
                type="danger" 
                @click="clearDatasetFile"
                :disabled="uploadingDataset"
              >清除文件</el-button>
            </div>
            <div v-if="datasetUploadStatus" class="upload-status">
              <el-tag :type="datasetUploadStatus.type" size="small">
                {{ datasetUploadStatus.message }}
              </el-tag>
            </div>
          </div>
          <!-- 训练参数配置 -->
          <div class="card">
            <h2>训练参数配置</h2>
            <div class="params-content">
              <div class="param-item">
                <span class="param-label">数据集:</span>
                <el-select v-model="selectedDataset" placeholder="选择数据集" @change="handleDatasetChange">
                  <el-option v-for="item in datasetList" :key="item" :label="item" :value="item" />
                </el-select>
              </div>
              <div class="param-item">
                <span class="param-label">实验名:</span>
                <el-input v-model="trainParams.exp_name" maxlength="32" show-word-limit placeholder="自定义实验名" />
              </div>
              <div class="param-item">
                <span class="param-label">训练轮数:</span>
                <el-input-number v-model="trainParams.epochs" :min="1" :max="300" />
              </div>
              <div class="param-item">
                <span class="param-label">批次大小:</span>
                <el-input-number v-model="trainParams.batch_size" :min="1" :max="128" />
              </div>
              <div class="param-item">
                <span class="param-label">图片尺寸:</span>
                <el-input-number v-model="trainParams.img_size" :min="320" :max="1280" />
              </div>
              <!-- <div class="param-item">
                <span class="param-label">模型配置:</span>
                <el-input v-model="trainParams.model_cfg" maxlength="256" show-word-limit placeholder="模型配置文件路径" />
              </div>
              <div class="param-item">
                <span class="param-label">预训练权重:</span>
                <el-input v-model="trainParams.pretrained_weights" maxlength="256" show-word-limit placeholder="预训练权重文件路径" />
              </div> -->
            </div>
            <el-button 
              type="primary" 
              @click="handleStartTraining"
              :loading="training"
              :disabled="!selectedDataset"
              style="margin-top:18px;"
            >发起训练</el-button>
          </div>
          <!-- 训练进度与结果展示 -->
          <div class="card">
            <h2>训练进度与结果</h2>
            <div v-if="training" class="loading-container">
              <el-icon class="loading-icon"><Loading /></el-icon>
              <p>训练中，请稍候...</p>
            </div>
            <div v-else-if="trainResult" class="result-section">
              <el-button type="success" @click="handleDownloadModel" style="margin-top:18px;">下载模型</el-button>
              <el-card class="stats-card">
                <template #header>
                  <span>主要指标</span>
                </template>
                <el-table :data="mainMetricsTableData" border style="width: 100%">
                  <el-table-column prop="label" label="参数" width="180"/>
                  <el-table-column prop="value" label="值"/>
                </el-table>
              </el-card>
              <div v-if="trainResult?.result_images?.length" class="result-image-container">
                <el-card class="image-card" v-for="img in trainResult.result_images" :key="img.name">
                  <template #header>
                    <span>{{ img.name }}</span>
                  </template>
                  <div class="image-wrapper">
                    <img :src="img.base64" class="result-image" :alt="img.name" />
                  </div>
                </el-card>
              </div>
              <el-card class="log-card">
                <template #header>
                  <span>训练日志</span>
                  <el-button type="primary" size="small" @click="downloadLog" style="float:right;">下载日志</el-button>
                </template>
                <div class="log-content-scroll">
                  <pre>{{ trainResult.train_log }}</pre>
                </div>
              </el-card>
             
            </div>
            <div v-else class="empty-result">
              <el-empty description="暂无训练结果" />
            </div>
          </div>
        </div>
        <el-backtop :right="40" :bottom="40" />
      </div>
    </div>
  </div>

</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Upload, Loading } from '@element-plus/icons-vue';
import CommonHeader from '@/components/CommonHeader.vue';
import CommonBreadcrumb from '@/components/CommonBreadcrumb.vue';
import CommonSidebar from '@/components/CommonSidebar.vue';
import { trainingApi } from '@/api/training';
import { useUserStore } from '@/stores/user';

const currentTime = ref(new Date().toLocaleString());
const theme = ref('dark');
const pageTitle = ref('智能训练');
const pageDescription = ref('上传数据集并配置参数，发起训练，查看训练进度与结果。');
const breadcrumbPaths = computed(() => [
  { name: '首页', path: '/' },
  { name: '智能训练', path: '/smart-training' }
]);

const mainMetrics = [
  { key: 'metrics/precision', label: 'Precision' },
  { key: 'metrics/recall', label: 'Recall' },
  { key: 'metrics/mAP_0.5', label: 'mAP@0.5' },
  { key: 'metrics/mAP_0.5:0.95', label: 'mAP@0.95' }
];
const mainMetricsTableData = computed(() => {
  if (!trainResult.value || !trainResult.value.metrics) return [];
  return mainMetrics.map(item => ({
    label: item.label,
    value: trainResult.value.metrics[item.key] ?? '-'
  }));
});

// 数据集上传与选择
const selectedDatasetFile = ref<File|null>(null);
const uploadingDataset = ref(false);
const datasetUploadStatus = ref<{ type: string; message: string } | null>(null);
const datasetList = ref<string[]>([]);
const selectedDataset = ref('');
const handleDatasetChange = (uploadFile: any) => {
  const file = uploadFile?.raw;
  if (file instanceof File) {
    selectedDatasetFile.value = file;
    ElMessage.success('数据集已准备好，请点击上传');
  }
  // 用户未选文件或取消时静默，不弹失败
};
const clearDatasetFile = () => {
  selectedDatasetFile.value = null;
  datasetUploadStatus.value = null;
};
const handleUploadDataset = async () => {
  if (!selectedDatasetFile.value) return;
  uploadingDataset.value = true;
  try {
    const res = await trainingApi.uploadDataset(selectedDatasetFile.value);
    if (res.code === 200 || res.success) {
      datasetUploadStatus.value = { type: 'success', message: res.info || res.message || '上传成功' };
      await refreshDatasetList();
    } else {
      datasetUploadStatus.value = { type: 'danger', message: res.info || res.message || '上传失败' };
    }
  } catch (e: any) {
    datasetUploadStatus.value = { type: 'danger', message: e?.response?.data?.info || e?.message || '上传失败' };
  } finally {
    uploadingDataset.value = false;
  }
};
const refreshDatasetList = async () => {
  try {
    const list = await trainingApi.listDatasets();
    datasetList.value = list;
    if (list.length && !selectedDataset.value) selectedDataset.value = list[0];
  } catch (e: any) {
    ElMessage.error(e?.response?.data?.info || e?.message || '获取数据集列表失败');
  }
};

// 训练参数
const trainParams = ref({
  zip_filename: '',
  exp_name: '',
  epochs: 50,
  batch_size: 8,
  img_size: 640,
  model_cfg: '',
  pretrained_weights: ''
});
const training = ref(false);
const trainResult = ref<any>(null);
const resultImages = ref<string[]>([]);
const trainLog = ref('');
const metricsDisplay = computed(() => {
  if (!trainResult.value) return {};
  const metrics = trainResult.value.metrics || trainResult.value.result || trainResult.value;
  const map: Record<string, any> = {};
  if (metrics) {
    for (const key in metrics) {
      if (['precision', 'mAP', 'recall', 'f1', 'mAP_0.5', 'mAP_0.5:0.95'].includes(key)) {
        map[key] = metrics[key];
      }
    }
  }
  return map;
});
const handleStartTraining = async () => {
  if (!selectedDataset.value) {
    ElMessage.error('请先选择数据集');
    return;
  }
  training.value = true;
  trainResult.value = null;
  resultImages.value = [];
  trainLog.value = '';
  try {
    const params = { ...trainParams.value, zip_filename: selectedDataset.value };
    const res = await trainingApi.trainYolo(params);
    if (res.code === 200 || res.success) {
      trainResult.value = res.data || res;
      const exp = params.exp_name || selectedDataset.value;
      resultImages.value = [
        'results.png',
        'confusion_matrix.png',
        'PR_curve.png',
        'F1_curve.png',
        'P_curve.png',
        'R_curve.png',
        'val_batch0_labels.jpg',
        'val_batch0_pred.jpg'
      ].map(name => trainingApi.getTrainResultImageUrl(exp) + `&img=${name}`);
      trainLog.value = res.data?.log || res.data?.train_log || res.log || res.train_log || '';
      ElMessage.success(res.info || res.message || '训练完成');
    } else {
      ElMessage.error(res.info || res.message || '训练失败');
    }
  } catch (e: any) {
    ElMessage.error(e?.response?.data?.info || e?.message || '训练失败');
  } finally {
    training.value = false;
  }
};
const downloadLog = () => {
  if (!trainResult.value?.train_log) return;
  const blob = new Blob([trainResult.value.train_log], { type: 'text/plain' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = (trainResult.value.exp_name || 'train_log') + '.txt';
  a.click();
  URL.revokeObjectURL(url);
};
const handleDownloadModel = async () => {
  if (!trainResult.value?.exp_name) {
    ElMessage.error('暂无可下载模型');
    return;
  }
  try {
    const blob = await trainingApi.downloadModel(trainResult.value.exp_name);
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = (trainResult.value.exp_name || 'model') + '.pt';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
    ElMessage.success('模型下载成功');
  } catch (e: any) {
    ElMessage.error(e?.response?.data?.info || e?.message || '模型下载失败');
  }
};
const formatFileSize = (size: number) => {
  if (size < 1024) return size + ' B';
  if (size < 1024 * 1024) return (size / 1024).toFixed(2) + ' KB';
  if (size < 1024 * 1024 * 1024) return (size / (1024 * 1024)).toFixed(2) + ' MB';
  return (size / (1024 * 1024 * 1024)).toFixed(2) + ' GB';
};

onMounted(() => {
  refreshDatasetList();
  setInterval(() => {
    currentTime.value = new Date().toLocaleString();
  }, 1000);
});
</script>

<style scoped>
@import "../common-features.scss";
.log-content-scroll {
  max-height: 300px;
  overflow: auto;
  background: #181c20;
  border-radius: 6px;
  padding: 16px;
  font-family: 'Fira Mono', 'Consolas', monospace;
  font-size: 14px;
  color: #e0e0e0;
}
</style> 