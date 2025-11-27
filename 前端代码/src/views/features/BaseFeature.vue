<template>
  <div class="feature-page dark-theme">
    <CommonHeader :currentTime="currentTime" :theme="theme" @toggleTheme="toggleTheme" />
    <div class="main-content">
      <CommonSidebar :isCollapse="isCollapse" />
      <div class="main">
        <CommonBreadcrumb :paths="breadcrumbPaths" />
        
        <div class="content-section">
          <!-- 功能介绍部分 -->
          <div class="feature-intro">
            <h2>{{ title }}</h2>
            <p class="description">{{ description }}</p>
          </div>

          <!-- 功能展示部分 -->
          <div class="feature-demo">
            <div class="upload-section">
              <div class="preview-container" v-if="imageUrl">
                <img :src="imageUrl" class="preview-image" />
                <div class="image-actions">
                  <el-button type="danger" @click="handleRemove">删除图片</el-button>
                  <el-button type="primary" @click="handleDetect" :loading="detecting">
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
              <slot name="result" :result="result">
                <!-- 默认结果展示 -->
                <pre v-if="result" class="result-content">{{ JSON.stringify(result, null, 2) }}</pre>
                <div v-else class="empty-result">请上传并处理图片</div>
              </slot>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { Upload } from '@element-plus/icons-vue';
import CommonHeader from '@/components/CommonHeader.vue';
import CommonSidebar from '@/components/CommonSidebar.vue';
import CommonBreadcrumb from '@/components/CommonBreadcrumb.vue';

const props = defineProps<{
  title: string;
  description: string;
  featureKey: string;
}>();

const currentTime = ref(new Date().toLocaleString());
const theme = ref('dark');
const isCollapse = ref(false);
const imageUrl = ref('');
const detecting = ref(false);
const result = ref(null);
const originalFile = ref(null);

// 面包屑路径
const breadcrumbPaths = computed(() => [
  { name: '首页', path: '/' },
  { name: '功能详情', path: '/features' },
  { name: props.title, path: '' }
]);

// 处理文件改变
const handleChange = (uploadFile: any) => {
  // console.log('上传组件传入的对象:', uploadFile);
  
  // Element Plus上传组件会传入有raw属性的对象
  const file = uploadFile.raw;
  
  if (!file) {
    // console.error('未找到文件对象，上传组件传入:', uploadFile);
    ElMessage.error('文件上传失败');
    return;
  }
  
  // console.log('获取到的文件对象:', file);
  
  // 校验是否为图片
  if (!file.type || !file.type.startsWith('image/')) {
    ElMessage.error('只能上传图片文件！');
    return;
  }
  
  // 校验是否为JPG或PNG
  if (!['image/jpeg', 'image/jpg', 'image/png'].includes(file.type)) {
    ElMessage.error('只支持JPG或PNG格式的图片！');
    return;
  }

  // 保存原始文件对象
  originalFile.value = file;
  
  // 创建预览URL
  imageUrl.value = URL.createObjectURL(file);
  
  // 显示上传成功提示
  ElMessage.success('图片已准备好，请点击"开始检测"按钮');
};

// 移除图片
const handleRemove = () => {
  imageUrl.value = '';
  originalFile.value = null;
  result.value = null;
};

// 开始检测
const handleDetect = async () => {
  detecting.value = true;
  try {
    if (!imageUrl.value || !originalFile.value) {
      ElMessage.error('请先上传图片');
      return;
    }

    // console.log('准备发送文件:', originalFile.value);
    // console.log('文件详情:', {
    //   name: originalFile.value.name || '未知',
    //   size: originalFile.value.size || 0,
    //   type: originalFile.value.type || '未知'
    // });
    
    ElMessage.info('正在处理图片，请稍候...');
    
    // 传递原始文件对象给处理函数
    const detectResult = await emit('detect', originalFile.value);
    
    result.value = detectResult;

    if (result.value && result.value.status === 'success') {
      ElMessage.success('检测完成');
    } else if (result.value) {
      ElMessage.warning(result.value.message || '检测结果异常');
    }
  } catch (error) {
    // console.error('检测失败:', error);
    ElMessage.error('检测失败: ' + (error instanceof Error ? error.message : String(error)));
    result.value = {
      status: 'error',
      message: '检测失败',
      data: {}
    };
  } finally {
    detecting.value = false;
  }
};

// 定义事件
const emit = defineEmits<{
  (e: 'detect', file: File): Promise<any>;
}>();

// 更新时间
setInterval(() => {
  currentTime.value = new Date().toLocaleString();
}, 1000);
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
  background-color: #1a1c1e;
}

.content-section {
  background-color: #282b30;
  border-radius: 8px;
  padding: 24px;
}

.feature-intro {
  margin-bottom: 32px;
}

.feature-intro h2 {
  color: #fff;
  margin: 0 0 16px 0;
}

.description {
  color: #8c8c8c;
  font-size: 14px;
  line-height: 1.6;
  margin: 0;
}

.feature-demo {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.upload-section {
  background-color: #1a1c1e;
  border-radius: 8px;
  padding: 24px;
}

.preview-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.preview-image {
  max-width: 100%;
  max-height: 400px;
  border-radius: 4px;
}

.image-actions {
  display: flex;
  gap: 16px;
}

.upload-area {
  width: 100%;
}

:deep(.el-upload-dragger) {
  width: 100%;
  height: 200px;
  background-color: #282b30;
  border: 2px dashed #404040;
}

:deep(.el-upload-dragger:hover) {
  border-color: var(--el-color-primary);
}

.upload-icon {
  font-size: 48px;
  color: #8c8c8c;
  margin-bottom: 16px;
}

.upload-text {
  color: #8c8c8c;
}

.upload-text em {
  color: var(--el-color-primary);
  font-style: normal;
}

.result-section {
  background-color: #1a1c1e;
  border-radius: 8px;
  padding: 24px;
}

.result-section h3 {
  color: #fff;
  margin: 0 0 16px 0;
}

.result-content {
  background-color: #282b30;
  border-radius: 4px;
  padding: 16px;
  color: #8c8c8c;
  font-family: monospace;
  margin: 0;
  white-space: pre-wrap;
}

.empty-result {
  background-color: #282b30;
  border-radius: 4px;
  padding: 16px;
  color: #8c8c8c;
  text-align: center;
  font-size: 14px;
}
</style> 