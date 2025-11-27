<!--
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-07-06 12:00:00
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-07-14 00:57:29
 * @FilePath: \front\src\views\features\EnhancedDetection.vue
 * @Description: 增强版检测组件，支持模型激活状态、置信度/IoU滑块控制
-->
<template>
  <div class="feature-page dark-theme">
    <CommonHeader :currentTime="currentTime" :theme="theme" @toggleTheme="toggleTheme" />
    <div class="main-content">
      <CommonSidebar />
      <div class="main">
        <CommonBreadcrumb :paths="breadcrumbPaths" />
        
        <div class="content-section">
          <!-- 功能介绍部分 -->
          <div class="card">
            <h2>{{ modelInfoDetail && modelInfoDetail.title ? modelInfoDetail.title : (modelInfoDetail && modelInfoDetail.fileName ? modelInfoDetail.fileName : '智能检测') }}</h2>
            <div v-if="modelInfoDetail && modelInfoDetail.description" class="model-desc">
              {{ modelInfoDetail.description }}
            </div>
            <p v-else class="description">暂无模型描述</p>
            <!-- <div style="color:#888; font-size:13px; margin-top:8px;">调试: {{ modelInfoDetail }}</div> -->
          </div>

          <!-- 图片检测区块 -->
          <div class="card">            <!-- 模型激活状态和选择 -->
            <div class="card">
              <div class="status-header">
                <h3>模型状态</h3>
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="refreshModelStatus"
                  :loading="refreshingStatus"
                >
                  刷新状态
                </el-button>
              </div>
              <div class="status-content">
                <div class="status-item">
                  <span class="label">当前模型:</span>
                  <span class="value">{{ currentModel || '未加载' }}</span>
                </div>
                <div class="status-item">
                  <span class="label">激活状态:</span>
                  <el-tag 
                    :type="modelLoaded ? 'success' : 'danger'"
                    size="small"
                  >
                    {{ modelLoaded ? '已激活' : '未激活' }}
                  </el-tag>
                </div>
                <div v-if="modelInfo" class="status-item">
                  <span class="label">加载时间:</span>
                  <span class="value">{{ formatTime(modelInfo.load_time) }}</span>
                </div>
                <div v-if="modelClassNames.length" class="status-item">
                  <span class="label">类别:</span>
                  <span class="value">{{ modelClassNames.join(', ') }}</span>
                </div>
              </div>
              <div class="model-selection">
                <el-select 
                  v-model="selectedModel" 
                  placeholder="选择检测模型"
                  @change="handleModelChange"
                  :disabled="loadingModel"
                >
                  <el-option
                    v-for="model in modelList"
                    :key="model"
                    :label="model"
                    :value="model"
                  />
                </el-select>
                <el-button 
                  type="success" 
                  @click="loadModel"
                  :loading="loadingModel"
                  :disabled="!selectedModel"
                >
                  激活模型
                </el-button>
              </div>
              <!-- 上传新模型美化布局 -->
              <div class="upload-row">
                <span class="upload-label">上传新模型</span>
                <el-upload
                  ref="modelUploadRef"
                  class="model-upload"
                  action="#"
                  :auto-upload="false"
                  :show-file-list="false"
                  :on-change="handleModelFileChange"
                  accept=".pt"
                  :disabled="uploadingModel"
                >
                  <el-button 
                    type="primary" 
                    :loading="uploadingModel"
                    :disabled="uploadingModel"
                  >
                    <el-icon><Upload /></el-icon>
                    选择PT文件
                  </el-button>
                </el-upload>
                <el-button 
                  v-if="selectedModelFile"
                  type="success" 
                  @click="openPtDialog"
                  :loading="uploadingModel"
                  :disabled="!selectedModelFile"
                >
                  上传模型
                </el-button>
                <el-button 
                  v-if="selectedModelFile"
                  type="danger" 
                  @click="clearModelFile"
                  :disabled="uploadingModel"
                >
                  清除文件
                </el-button>
              </div>
              <div v-if="selectedModelFile" class="file-info">
                <span class="file-name">{{ selectedModelFile.name }}</span>
                <span class="file-size">{{ formatFileSize(selectedModelFile.size) }}</span>
              </div>
              <div v-if="uploadStatus" class="upload-status">
                <el-tag :type="uploadStatus.type" size="small">
                  {{ uploadStatus.message }}
                </el-tag>
              </div>
            </div>
            <!-- 检测参数设置 -->
            <div class="card">
              <h3>检测参数</h3>
              <div class="params-content">
                <div class="param-item">
                  <span class="param-label">置信度阈值:</span>
                  <div class="slider-container">
                    <el-slider
                      v-model="detectionParams.confThreshold"
                      :min="0"
                      :max="1"
                      :step="0.05"
                      :show-tooltip="true"
                      :format-tooltip="formatTooltip"
                    />
                    <span class="param-value">{{ (detectionParams.confThreshold * 100).toFixed(0) }}%</span>
                  </div>
                </div>
                <div class="param-item">
                  <span class="param-label">IoU阈值:</span>
                  <div class="slider-container">
                    <el-slider
                      v-model="detectionParams.iouThreshold"
                      :min="0"
                      :max="1"
                      :step="0.05"
                      :show-tooltip="true"
                      :format-tooltip="formatTooltip"
                    />
                    <span class="param-value">{{ (detectionParams.iouThreshold * 100).toFixed(0) }}%</span>
                  </div>
                </div>
                <div class="param-item" v-if="modelClassNames.length">
                  <span class="param-label">需要检测的种类:</span>
                  <el-checkbox-group v-model="selectedClasses">
                    <el-checkbox v-for="item in modelClassNames" :key="item" :label="item">{{ item }}</el-checkbox>
                  </el-checkbox-group>
                </div>
              </div>
            </div>
            <h2>图片检测</h2>
            <!-- 功能展示部分 -->
            <div class="card">
              <div class="upload-section">
                <div class="preview-container" v-if="imageUrl">
                  <img :src="imageUrl" class="preview-image" />
                  <div class="image-actions">
                    <el-button type="danger" @click="handleRemove">删除图片</el-button>
                    <el-button 
                      type="primary" 
                      @click="handleDetect" 
                      :loading="detecting"
                      :disabled="!modelLoaded"
                    >
                      开始检测
                    </el-button>
                  </div>
                </div>
                <el-upload
                  v-else
                  class="upload-area"
                  drag
                  action="#"
                  :auto-upload="false"
                  :show-file-list="false"
                  :on-change="handleChange"
                  accept="image/*"
                >
                  <el-icon class="upload-icon"><Upload /></el-icon>
                  <div class="upload-text">
                    <span>将图片拖到此处，或<em>点击上传</em></span>
                    <p>支持 jpg、png 格式图片</p>
                  </div>
                </el-upload>
              </div>

              <!-- 检测结果展示 -->
              <div class="result-section">
                <h3>检测结果</h3>
                <div v-if="isLoading" class="loading-container">
                  <el-icon class="loading-icon"><Loading /></el-icon>
                  <p>正在处理图片，请稍候...</p>
                </div>
                <div v-else-if="detectionResult && detectionResult.detections" class="detection-result">
                  <!-- 检测统计 -->
                  <el-card class="stats-card">
                    <template #header>
                      <span>检测统计</span>
                    </template>
                    <div class="stats-content">
                      <div class="stat-item">
                        <span class="stat-label">检测目标数:</span>
                        <span class="stat-value">{{ detectionResult.detections.length || 0 }}</span>
                      </div>
                      <div class="stat-item">
                        <span class="stat-label">平均置信度:</span>
                        <span class="stat-value">{{ averageConfidence }}%</span>
                      </div>
                    </div>
                  </el-card>

                  <!-- 结果图片 -->
                  <div v-if="resultImage" class="result-image-container">
                    <el-card class="image-card">
                      <template #header>
                        <span>检测结果图片</span>
                      </template>
                      <div class="image-wrapper">
                        <img :src="resultImage" class="result-image" alt="检测结果" />
                      </div>
                    </el-card>
                  </div>

                  <!-- 检测详情 -->
                  <div v-if="detectionResult.detections.length > 0" class="detection-details">
                    <el-card class="details-card">
                      <template #header>
                        <span>检测详情</span>
                      </template>
                      <div class="detection-list">
                        <div 
                          v-for="(detection, index) in detectionResult.detections" 
                          :key="index"
                          class="detection-item"
                        >
                          <div class="detection-header">
                            <span class="detection-title">目标 {{ index + 1 }}</span>
                            <el-tag 
                              :type="getConfidenceColor(detection.confidence)"
                              size="small"
                            >
                              {{ (detection.confidence * 100).toFixed(1) }}%
                            </el-tag>
                          </div>
                          <div class="detection-info">
                            <div class="info-item">
                              <span class="info-label">类别:</span>
                              <span class="info-value">{{ detection.className }}</span>
                            </div>
                            <div class="info-item">
                              <span class="info-label">位置:</span>
                              <span class="info-value">[{{ detection.bbox.join(', ') }}]</span>
                            </div>
                          </div>
                        </div>
                      </div>
                    </el-card>
                  </div>
                  
                </div>
                <div v-else class="empty-result">
                  <el-empty description="请上传并处理图片" />
                </div>
              </div>
            </div>
          </div>

          <!-- 视频检测区块 -->
          <div class="card">
            <h2>视频检测</h2>
            <div class="video-detect-section">
              <div class="upload-section">
                <div class="preview-container" v-if="videoUrl">
                  <video :src="videoUrl" class="preview-video" controls style="max-width:100%;max-height:300px;" />
                  <div class="image-actions">
                    <el-button type="danger" @click="handleRemoveVideo">删除视频</el-button>
                    <el-button 
                      type="primary" 
                      @click="handleDetectVideo" 
                      :loading="detectingVideo"
                      :disabled="!modelLoaded"
                    >
                      开始检测
                    </el-button>
                  </div>
                </div>
                <el-upload
                  v-else
                  class="upload-area"
                  drag
                  action="#"
                  :auto-upload="false"
                  :show-file-list="false"
                  :on-change="handleChangeVideo"
                  accept="video/*"
                >
                  <el-icon class="upload-icon"><Upload /></el-icon>
                  <div class="upload-text">
                    <span>将视频拖到此处，或<em>点击上传</em></span>
                    <p>支持 mp4、avi、mov、mkv 格式视频</p>
                  </div>
                </el-upload>
                <!-- 新增：跳帧数设置 -->
                <div style="margin-top: 12px; display: flex; align-items: center; gap: 8px;">
                  <span>跳帧数：</span>
                  <el-input-number v-model="videoFrameInterval" :min="1" :step="1" style="width: 100px;" />
                  <span style="color:#888;font-size:13px;">（每隔N帧检测，1为每帧检测）</span>
                </div>
              </div>
              <div class="result-section">
                <h3>检测结果</h3>
                <div v-if="isLoadingVideo" class="loading-container">
                  <el-icon class="loading-icon"><Loading /></el-icon>
                  <p>正在处理视频，请稍候...</p>
                </div>
                <div v-else-if="videoDetectionResult && videoDetectionResult.processedVideoBase64" class="detection-result">
                  <el-card class="stats-card">
                    <template #header>
                      <span>检测统计</span>
                    </template>
                    <div class="stats-content">
                      <div class="stat-item">
                        <span class="stat-label">总帧数:</span>
                        <span class="stat-value">{{ videoDetectionResult.videoInfo?.fps ? videoDetectionResult.totalFrames : '-' }}</span>
                      </div>
                      <div class="stat-item">
                        <span class="stat-label">检测目标数:</span>
                        <span class="stat-value">{{ videoDetectionResult.totalDetections || 0 }}</span>
                      </div>
                    </div>
                  </el-card>
                  <div v-if="videoDetectionResult.processedVideoBase64" class="result-video-container">
                    <el-card class="image-card">
                      <template #header>
                        <span>检测后视频</span>
                      </template>
                      <div class="image-wrapper">
                        <video :src="'data:video/mp4;base64,' + videoDetectionResult.processedVideoBase64" controls style="max-width:100%;max-height:300px;" />
                      </div>
                    </el-card>
                  </div>
                </div>
                <div v-else class="empty-result">
                  <el-empty description="请上传并处理视频" />
                </div>
              </div>
            </div>
          </div>
        </div>
        <el-backtop :right="40" :bottom="40" />
      </div>
    </div>
  </div>
  <el-dialog
    v-model="showPtDialog"
    title="上传PT模型信息"
    width="400px"
    :close-on-click-modal="false"
  >
    <el-form :model="ptForm" label-width="80px">
      <el-form-item label="模型标题">
        <el-input v-model="ptForm.title" maxlength="32" show-word-limit placeholder="请输入模型标题" />
      </el-form-item>
      <el-form-item label="模型描述">
        <el-input
          v-model="ptForm.desc"
          type="textarea"
          :rows="3"
          maxlength="128"
          show-word-limit
          placeholder="请输入模型描述"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="showPtDialog = false">取消</el-button>
      <el-button type="primary" :loading="ptFormLoading" @click="handlePtDialogOk">上传</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Upload, Loading } from '@element-plus/icons-vue';
import CommonHeader from '@/components/CommonHeader.vue';
import CommonBreadcrumb from '@/components/CommonBreadcrumb.vue';
import { useYoloModelStore } from '@/stores/yoloModel';
import { yoloApi, type DetectionResult } from '@/api/yolo';
import { defineProps } from 'vue';
import CommonSidebar from '@/components/CommonSidebar.vue';

// Props
const props = defineProps<{
  title: string;
  description: string;
  featureKey: string;
  defaultModel?: string;
}>();

// 响应式数据
const currentTime = ref(new Date().toLocaleString());
const theme = ref('dark');
const imageUrl = ref('');
const detecting = ref(false);
const isLoading = ref(false);
const originalFile = ref(null);
const resultImage = ref('');
const detectionResult = ref<DetectionResult | null>(null);

// 模型相关
const modelList = ref<string[]>([]);
const selectedModel = ref('');
const yoloModelStore = useYoloModelStore();
const loadingModel = ref(false);
const refreshingStatus = ref(false);
const modelClassNames = ref<string[]>([]);
const selectedClasses = ref<string[]>([]);
const modelInfoDetail = ref(null);

// 检测参数
const detectionParams = ref({
  confThreshold: 0.25,
  iouThreshold: 0.45
});

// 面包屑路径
const breadcrumbPaths = computed(() => [
  { name: '首页', path: '/' },
  { name: '功能详情', path: '/features' },
  { name: props.title, path: '' }
]);

// 计算属性
const averageConfidence = computed(() => {
  if (!detectionResult.value?.detections?.length) return 0;
  const total = detectionResult.value.detections.reduce((sum: number, d: any) => sum + d.confidence, 0);
  return ((total / detectionResult.value.detections.length) * 100).toFixed(1);
});

// 方法
const toggleTheme = () => {
  theme.value = theme.value === 'dark' ? 'light' : 'dark';
};

const formatTime = (timeStr: string) => {
  if (!timeStr) return '';
  try {
    return new Date(timeStr).toLocaleString();
  } catch {
    return timeStr;
  }
};

const getConfidenceColor = (confidence: number) => {
  // 所有模型置信度越高越报警
  if (confidence > 0.8) return 'danger';
  if (confidence > 0.5) return 'warning';
  return 'success';
};

const formatTooltip = (val: number) => `${(val * 100).toFixed(0)}%`;

// 初始化
const initialize = async () => {
  try {
    // 获取模型列表
    modelList.value = await yoloApi.getModelList();
    if (props.defaultModel && modelList.value.includes(props.defaultModel)) {
      selectedModel.value = props.defaultModel;
    } else if (modelList.value.length > 0) {
      selectedModel.value = modelList.value[0];
    }
    // 获取模型状态
    await refreshModelStatus();
  } catch (error: any) {
    ElMessage.error(error?.info || error?.message || '初始化失败');
  }
};

// 刷新模型状态
const refreshModelStatus = async () => {
  try {
    refreshingStatus.value = true;
    const status = await yoloApi.getModelStatus();
    yoloModelStore.setModelStatus(status);
    // 同时重新获取模型列表
    const models = await yoloApi.getModelList();
    modelList.value = models;
    if (selectedModel.value && !models.includes(selectedModel.value)) {
      selectedModel.value = '';
    }
    if (status.modelInfo && status.modelInfo.class_names) {
      modelClassNames.value = status.modelInfo.class_names;
      selectedClasses.value = [...status.modelInfo.class_names];
    } else {
      modelClassNames.value = [];
      selectedClasses.value = [];
    }
  } catch (error: any) {
    ElMessage.error(error?.info || error?.message || '获取模型状态失败');
  } finally {
    refreshingStatus.value = false;
  }
};

// 模型选择改变
const handleModelChange = () => {
  // 可以在这里添加模型切换的逻辑
};

// 加载模型
const loadModel = async () => {
  if (!selectedModel.value) {
    ElMessage.warning('请先选择模型');
    return;
  }
  try {
    loadingModel.value = true;
    const result = await yoloApi.loadModel(selectedModel.value);
    if (result.success) {
      ElMessage.success(result.message || '模型加载成功');
      await refreshModelStatus();
      if (result.model_info && result.model_info.class_names) {
        modelClassNames.value = result.model_info.class_names;
        selectedClasses.value = [...result.model_info.class_names];
      }
    } else {
      ElMessage.error(result.message || '模型加载失败');
    }
  } catch (error: any) {
    ElMessage.error(error?.info || error?.message || '模型加载失败');
  } finally {
    loadingModel.value = false;
  }
};

// 文件处理
const handleChange = (uploadFile: any) => {
  const file = uploadFile.raw;
  
  if (!file) {
    ElMessage.error('文件上传失败');
    return;
  }
  
  if (!file.type || !file.type.startsWith('image/')) {
    ElMessage.error('只能上传图片文件！');
    return;
  }
  
  if (!['image/jpeg', 'image/jpg', 'image/png'].includes(file.type)) {
    ElMessage.error('只支持JPG或PNG格式的图片！');
    return;
  }

  originalFile.value = file;
  imageUrl.value = URL.createObjectURL(file);
  ElMessage.success('图片已准备好，请点击"开始检测"按钮');
};

const handleRemove = () => {
  imageUrl.value = '';
  originalFile.value = null;
  resultImage.value = '';
  detectionResult.value = null;
};

// 开始检测
const handleDetect = async () => {
  if (!modelLoaded.value) {
    ElMessage.warning('请先激活模型');
    return;
  }
  if (!imageUrl.value || !originalFile.value) {
    ElMessage.error('请先上传图片');
    return;
  }
  try {
    detecting.value = true;
    isLoading.value = true;
    resultImage.value = '';
    detectionResult.value = null;
    ElMessage.info('正在处理图片，请稍候...');
    const imageBase64 = await yoloApi.fileToBase64(originalFile.value);
    const params = {
      confThreshold: detectionParams.value.confThreshold,
      iouThreshold: detectionParams.value.iouThreshold,
      target_class_names: selectedClasses.value
    };
    const result = await yoloApi.detectImage(currentModel.value, imageBase64, params);
    if (result.success) {
      detectionResult.value = result;
      if (result.annotatedImageBase64) {
        resultImage.value = 'data:image/jpeg;base64,' + result.annotatedImageBase64;
      }
      if (result.detections && result.detections.length === 0) {
        ElMessage.warning('未检测到任何目标，模型可能不匹配或图片无目标');
      } else {
        ElMessage.success('检测完成');
      }
    } else {
      ElMessage.error(result.message || '检测失败');
    }
  } catch (error: any) {
    ElMessage.error(error?.info || error?.message || '检测失败');
  } finally {
    detecting.value = false;
    isLoading.value = false;
  }
};

// 页面渲染直接用store的状态
const currentModel = computed(() => yoloModelStore.currentModel);
const modelLoaded = computed(() => yoloModelStore.modelLoaded);
const modelInfo = computed(() => yoloModelStore.modelInfo);

// 移除本地 modelMetaMap，改为后端获取
const ptMetaMap = ref<Record<string, { title: string; description: string }>>({});

// 页面标题和说明
const pageTitle = ref('智能检测');
const pageDescription = ref('请选择模型以切换检测功能。');

// 监听模型切换，获取并渲染模型详细信息
watch(selectedModel, async (val) => {
  console.log('selectedModel:', val);
  if (val) {
    try {
      const info = await yoloApi.getPtInfo(val);
      console.log('getPtInfo 返回:', info);
      console.log('typeof info:', typeof info, Array.isArray(info), info);
      window._debugInfo = info;
      modelInfoDetail.value = info;
    } catch (e) {
      console.error('getPtInfo error:', e);
      modelInfoDetail.value = null;
    }
  } else {
    modelInfoDetail.value = null;
  }
}, { immediate: true });

// 初始化时获取PT元信息
onMounted(() => {
  initialize();
});

// 更新时间
setInterval(() => {
  currentTime.value = new Date().toLocaleString();
}, 1000);

const videoUrl = ref('');
const originalVideoFile = ref<File|null>(null);
const detectingVideo = ref(false);
const isLoadingVideo = ref(false);
const videoDetectionResult = ref<any>(null);
const videoFrameInterval = ref(1); // 跳帧数，默认1

const handleChangeVideo = (uploadFile: any) => {
  const file = uploadFile.raw;
  if (!file) {
    ElMessage.error('视频上传失败');
    return;
  }
  if (!file.type || !file.type.startsWith('video/')) {
    ElMessage.error('只能上传视频文件！');
    return;
  }
  originalVideoFile.value = file;
  videoUrl.value = URL.createObjectURL(file);
  ElMessage.success('视频已准备好，请点击"开始检测"按钮');
};

const handleRemoveVideo = () => {
  videoUrl.value = '';
  originalVideoFile.value = null;
  videoDetectionResult.value = null;
};

// 替换视频检测接口调用
const handleDetectVideo = async () => {
  if (!modelLoaded.value) {
    ElMessage.warning('请先激活模型');
    return;
  }
  if (!videoUrl.value || !originalVideoFile.value) {
    ElMessage.error('请先上传视频');
    return;
  }
  try {
    detectingVideo.value = true;
    isLoadingVideo.value = true;
    videoDetectionResult.value = null;
    ElMessage.info('正在处理视频，请稍候...');
    const videoBase64 = await yoloApi.fileToBase64(originalVideoFile.value);
    const params: Record<string, any> = {
      confThreshold: detectionParams.value.confThreshold,
      iouThreshold: detectionParams.value.iouThreshold,
      target_class_names: selectedClasses.value,
      frameInterval: videoFrameInterval.value // 新增跳帧参数
    };
    const result = await yoloApi.detectVideo(currentModel.value, videoBase64, params);
    if (result && result.processedVideoBase64) {
      videoDetectionResult.value = result;
      ElMessage.success('视频检测完成');
    } else {
      ElMessage.error(result?.info || '视频检测失败');
    }
  } catch (error: any) {
    ElMessage.error(error?.info || error?.message || '视频检测失败');
  } finally {
    detectingVideo.value = false;
    isLoadingVideo.value = false;
  }
};

const modelUploadRef = ref(null);
const selectedModelFile = ref<File|null>(null);
const uploadingModel = ref(false);
const uploadStatus = ref<{ type: string; message: string } | null>(null);

const handleModelFileChange = (uploadFile: any) => {
  const file = uploadFile.raw;
  if (!file) {
    ElMessage.error('模型文件上传失败');
    return;
  }
  if (!file.name.toLowerCase().endsWith('.pt')) {
    ElMessage.error('只能上传PT文件！');
    return;
  }
  selectedModelFile.value = file;
  ElMessage.success('模型文件已准备好，请点击"上传模型"按钮');
};

// 新增：自定义PT模型上传弹窗表单
const showPtDialog = ref(false);
const ptForm = ref({ title: '', desc: '' });
const ptFormLoading = ref(false);

const openPtDialog = () => {
  ptForm.value = { title: '', desc: '' };
  showPtDialog.value = true;
};

// 上传PT模型时传入 title/description
const handlePtDialogOk = async () => {
  if (!ptForm.value.title.trim()) {
    ElMessage.error('标题不能为空');
    return;
  }
  if (!ptForm.value.desc.trim()) {
    ElMessage.error('描述不能为空');
    return;
  }
  ptFormLoading.value = true;
  try {
    if (!selectedModelFile.value) {
      ElMessage.error('请先选择模型文件');
      ptFormLoading.value = false;
      return;
    }
    const result = await yoloApi.uploadModel(selectedModelFile.value, {
      title: ptForm.value.title,
      description: ptForm.value.desc
    });
    if (result.success) {
      ElMessage.success(result.message || '模型上传成功');
      await refreshModelStatus();
      selectedModelFile.value = null;
      uploadStatus.value = null;
      showPtDialog.value = false;
    } else {
      ElMessage.error(result.message || '模型上传失败');
    }
  } catch (e: any) {
    ElMessage.error(e?.info || e?.message || '模型上传失败');
  } finally {
    ptFormLoading.value = false;
  }
};

const clearModelFile = () => {
  selectedModelFile.value = null;
  uploadStatus.value = null;
};

const formatFileSize = (size: number) => {
  if (size < 1024) {
    return size + ' B';
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(2) + ' KB';
  } else if (size < 1024 * 1024 * 1024) {
    return (size / (1024 * 1024)).toFixed(2) + ' MB';
  } else {
    return (size / (1024 * 1024 * 1024)).toFixed(2) + ' GB';
  }
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

/* 统一内容区块样式 */
.card {
  margin-bottom: 32px;
  background: transparent;
  border: 1.5px solid #3a3f55;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.06);
  padding: 32px 28px 28px 28px;
  transition: box-shadow 0.2s;
}
.card:hover {
  box-shadow: 0 8px 32px rgba(0,0,0,0.12);
  border-color: #409eff;
}

.model-status-section {
  margin-bottom: 30px;
}

.model-status-card {
  background: linear-gradient(135deg, #2a2d3e 0%, #1a1c1e 100%);
  border-radius: 8px;
  border: 1px solid #3a3f55;
  padding: 20px;
}

.status-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.status-header h3 {
  color: #fff;
  margin: 0;
}

.status-content {
  margin-bottom: 20px;
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding: 8px 0;
  border-bottom: 1px solid #3a3f55;
}

.status-item:last-child {
  border-bottom: none;
}

.label {
  color: #b0b0b0;
  font-weight: 500;
}

.value {
  color: #fff;
}

.model-selection {
  display: flex;
  gap: 15px;
  align-items: center;
}

.model-selection .el-select {
  flex: 1;
}

.detection-params-section {
  margin-bottom: 30px;
}

.params-card {
  background: linear-gradient(135deg, #2a2d3e 0%, #1a1c1e 100%);
  border-radius: 8px;
  border: 1px solid #3a3f55;
  padding: 20px;
}

.params-card h3 {
  color: #fff;
  margin-bottom: 20px;
}

.params-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.param-item {
  display: flex;
  align-items: center;
  gap: 15px;
}

.param-label {
  color: #b0b0b0;
  min-width: 100px;
  font-weight: 500;
}

.slider-container {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 15px;
}

.param-value {
  color: #fff;
  min-width: 50px;
  text-align: right;
}

.upload-section {
  margin-bottom: 30px;
}

.preview-container {
  text-align: center;
}

.preview-image {
  max-width: 100%;
  max-height: 400px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.image-actions {
  margin-top: 15px;
  display: flex;
  justify-content: center;
  gap: 15px;
}

.upload-area {
  border: 2px dashed #3a3f55;
  border-radius: 8px;
  padding: 40px;
  text-align: center;
  background: rgba(58, 63, 85, 0.1);
  transition: all 0.3s;
}

.upload-area:hover {
  border-color: #409eff;
  background: rgba(64, 158, 255, 0.1);
}

.upload-icon {
  font-size: 48px;
  color: #409eff;
  margin-bottom: 15px;
}

.upload-text {
  color: #b0b0b0;
}

.upload-text em {
  color: #409eff;
  font-style: normal;
}

.result-section {
  margin-top: 30px;
}

.result-section h3 {
  color: #fff;
  margin-bottom: 20px;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
}

.loading-icon {
  font-size: 48px;
  color: #409eff;
  animation: rotate 2s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.loading-container p {
  margin-top: 16px;
  color: #8c8c8c;
}

.detection-result {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.result-stats {
  margin-bottom: 20px;
}

.stats-card {
  background: linear-gradient(135deg, #2a2d3e 0%, #1a1c1e 100%);
  border: 1px solid #3a3f55;
}

.stats-content {
  display: flex;
  gap: 30px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.stat-label {
  color: #b0b0b0;
  font-size: 14px;
}

.stat-value {
  color: #fff;
  font-size: 18px;
  font-weight: bold;
}

.detection-details {
  margin-bottom: 20px;
}

.details-card {
  background: linear-gradient(135deg, #2a2d3e 0%, #1a1c1e 100%);
  border: 1px solid #3a3f55;
}

.detection-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.detection-item {
  padding: 15px;
  background: rgba(58, 63, 85, 0.3);
  border-radius: 6px;
  border: 1px solid #3a3f55;
}

.detection-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.detection-title {
  color: #fff;
  font-weight: bold;
}

.detection-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-item {
  display: flex;
  gap: 10px;
}

.info-label {
  color: #b0b0b0;
  min-width: 60px;
}

.info-value {
  color: #fff;
}

.result-image-container {
  margin-top: 20px;
}

.image-card {
  background: linear-gradient(135deg, #2a2d3e 0%, #1a1c1e 100%);
  border: 1px solid #3a3f55;
}

.image-wrapper {
  display: flex;
  justify-content: center;
}

.result-image {
  max-width: 100%;
  border-radius: 6px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.empty-result {
  text-align: center;
  padding: 40px 0;
}

/* Element Plus 组件样式覆盖 */
:deep(.el-card) {
  background: transparent;
  border: none;
}

:deep(.el-card__header) {
  background: transparent;
  border-bottom: 1px solid #3a3f55;
  color: #fff;
}

:deep(.el-card__body) {
  background: transparent;
}

:deep(.el-select) {
  width: 100%;
}

:deep(.el-slider) {
  flex: 1;
}

:deep(.el-slider__runway) {
  background-color: #3a3f55;
}

:deep(.el-slider__bar) {
  background-color: #409eff;
}

:deep(.el-slider__button) {
  border-color: #409eff;
}

:deep(.el-tag) {
  border-radius: 4px;
}

:deep(.el-button) {
  border-radius: 6px;
}

:deep(.el-upload) {
  width: 100%;
}

:deep(.el-upload-dragger) {
  background: transparent;
  border: none;
  width: 100%;
}

.model-upload-section-inside {
  margin-top: 20px;
}

.upload-header-row {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 10px;
}

.upload-header-row h4 {
  color: #fff;
  margin: 0;
  font-size: 16px;
  font-weight: 500;
}

.file-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: rgba(58, 63, 85, 0.3);
  border-radius: 4px;
  margin-bottom: 10px;
}

.file-name {
  color: #fff;
  font-weight: 500;
}

.file-size {
  color: #b0b0b0;
  font-size: 12px;
}

.upload-status {
  margin-top: 10px;
}

.upload-row {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-top: 18px;
  margin-bottom: 6px;
}

.upload-label {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  min-width: 90px;
  line-height: 38px;
}

.model-upload .el-button {
  border-radius: 6px;
  font-weight: 500;
}

.upload-row .el-button {
  min-width: 110px;
  height: 38px;
  font-size: 15px;
  padding: 0 18px;
}

.file-info {
  margin-left: 104px;
  margin-top: 2px;
  color: #b0b0b0;
  font-size: 13px;
  display: flex;
  gap: 18px;
}
.model-desc {
  color: #b0b0b0;
  margin-top: 8px;
  font-size: 15px;
  line-height: 1.6;
}
</style> 