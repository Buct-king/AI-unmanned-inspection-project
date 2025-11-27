<template>
  <div class="feature-page dark-theme">
    <CommonHeader :currentTime="currentTime" :theme="theme" @toggleTheme="toggleTheme" />
    <div class="main-content">
      <CommonSidebar />
      <div class="main">
        <CommonBreadcrumb :paths="breadcrumbPaths" />
        <div class="content-section">
          <div class="card">
            <h2>检测后/原始视频展示</h2>
            <el-tabs v-model="activeTab">
              <el-tab-pane label="检测后视频" name="detect">
            <el-button type="primary" @click="fetchDetectVideos" :loading="loadingVideos">刷新列表</el-button>
            <el-table :data="detectVideoList" style="margin-top: 16px;">
              <el-table-column prop="filename" label="文件名" />
              <el-table-column label="操作">
                <template #default="{ row }">
                      <el-button size="small" @click="playDetectVideo(row.filename)">播放</el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </el-tab-pane>
              <el-tab-pane label="原始视频" name="raw">
                <el-button type="primary" @click="fetchRawVideos" :loading="loadingVideos">刷新列表</el-button>
                <el-table :data="rawVideoList" style="margin-top: 16px;">
                  <el-table-column prop="filename" label="文件名" />
                  <el-table-column label="操作">
                    <template #default="{ row }">
                      <el-button size="small" @click="playRawVideo(row.filename)">播放</el-button>
                </template>
              </el-table-column>
            </el-table>
              </el-tab-pane>
            </el-tabs>
            <div v-if="currentVideoUrl" class="video-player-center">
              <video :src="currentVideoUrl" controls class="big-video-player" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { yoloApi } from '@/api/yolo';
import { ElMessage } from 'element-plus';
import CommonHeader from '@/components/CommonHeader.vue';
import CommonSidebar from '@/components/CommonSidebar.vue';
import CommonBreadcrumb from '@/components/CommonBreadcrumb.vue';

const currentTime = ref(new Date().toLocaleString());
const theme = ref('dark');
const breadcrumbPaths = [
  { name: '首页', path: '/' },
  { name: '摄像头视频', path: '' }
];

const activeTab = ref('detect');
const rawVideoList = ref<{ filename: string }[]>([]);
const detectVideoList = ref<{ filename: string }[]>([]);
const loadingVideos = ref(false);
const currentVideoUrl = ref('');

const fetchRawVideos = async () => {
  loadingVideos.value = true;
  try {
    const list = await yoloApi.getRawVideoList();
    rawVideoList.value = (list || []).map(filename => ({ filename }));
  } catch (e: any) {
    ElMessage.error(e?.response?.data?.info || e?.message || '获取原始视频列表失败');
  } finally {
    loadingVideos.value = false;
  }
};
const fetchDetectVideos = async () => {
  loadingVideos.value = true;
  try {
    const list = await yoloApi.getDetectVideoList();
    detectVideoList.value = (list || []).map(filename => ({ filename }));
  } catch (e: any) {
    ElMessage.error(e?.response?.data?.info || e?.message || '获取检测后视频列表失败');
  } finally {
    loadingVideos.value = false;
  }
};

const playRawVideo = (filename: string) => {
  currentVideoUrl.value = yoloApi.getRawVideoUrl(filename);
};
const playDetectVideo = (filename: string) => {
  currentVideoUrl.value = yoloApi.getDetectVideoUrl(filename);
};

onMounted(() => {
  fetchRawVideos();
  fetchDetectVideos();
setInterval(() => {
  currentTime.value = new Date().toLocaleString();
}, 1000);
});

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
  width: 100%;
  max-width: 100vw;
  margin: 0 auto;
  padding: 0 2vw;
  box-sizing: border-box;
}
.card {
  margin-bottom: 2.5vw;
  background: transparent;
  border: 1.5px solid #3a3f55;
  border-radius: 1.2vw;
  box-shadow: 0 4px 16px rgba(0,0,0,0.06);
  padding: 2.5vw 2vw 2vw 2vw;
  transition: box-shadow 0.2s;
  width: 100%;
  max-width: 100vw;
  box-sizing: border-box;
}
.card:hover {
  box-shadow: 0 8px 32px rgba(0,0,0,0.12);
  border-color: #409eff;
}
.video-player-center {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 2vw;
  width: 100%;
}
.big-video-player {
  width: 100%;
  max-width: 900px;
  max-height: 60vw;
  background: #000;
  border-radius: 1vw;
  box-shadow: 0 2px 16px rgba(0,0,0,0.18);
}
@media (max-width: 900px) {
  .content-section, .card {
    padding: 0 2vw;
    border-radius: 2vw;
  }
  .big-video-player {
    max-width: 100vw;
    max-height: 60vw;
    border-radius: 2vw;
  }
}
@media (max-width: 600px) {
  .content-section, .card {
    padding: 0 1vw;
    border-radius: 3vw;
  }
  .big-video-player {
    max-width: 100vw;
    max-height: 60vw;
    border-radius: 3vw;
  }
}
</style> 