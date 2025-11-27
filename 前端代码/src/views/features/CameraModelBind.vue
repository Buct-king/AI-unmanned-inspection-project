<template>
  <div class="feature-page dark-theme">
    <CommonHeader :currentTime="currentTime" :theme="theme" @toggleTheme="toggleTheme" />
    <div class="main-content">
      <CommonSidebar />
      <div class="main">
        <CommonBreadcrumb :paths="breadcrumbPaths" />
        <div class="content-section">
          <div class="card">
            <h2>摄像头与模型绑定管理</h2>
            <el-button type="primary" @click="openAddDialog" style="margin-bottom: 16px;">新增绑定</el-button>
            <el-table :data="bindList" style="margin-bottom: 16px;">
              <el-table-column prop="cameraName" label="摄像头名称" />
              <el-table-column prop="cameraId" label="摄像头ID" />
              <el-table-column prop="cameraSource" label="摄像头源" />
              <el-table-column prop="modelName" label="模型文件" />
              <el-table-column label="操作">
                <template #default="{ row }">
                  <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
                  <el-button size="small" type="danger" @click="deleteBind(row.id)">删除</el-button>
                  <el-button size="small" type="success" @click="detectBind(row.id)">检测</el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-button @click="fetchBindList" :loading="loading">刷新列表</el-button>
          </div>
        </div>
      </div>
    </div>
    <el-dialog v-model="dialogVisible" :title="dialogMode === 'add' ? '新增绑定' : '编辑绑定'">
      <el-form :model="form" label-width="100px">
        <el-form-item label="摄像头名称">
          <el-input v-model="form.cameraName" />
        </el-form-item>
        <el-form-item label="摄像头ID">
          <el-input v-model="form.cameraId" />
        </el-form-item>
        <el-form-item label="摄像头源">
          <el-input v-model="form.cameraSource" />
          <el-button size="small" @click="openRtspDialog">自动检测RTSP</el-button>
        </el-form-item>
        <el-form-item label="模型文件">
          <el-input v-model="form.modelName" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
    <el-dialog v-model="rtspDialogVisible" title="自动检测RTSP流">
      <el-form :model="rtspForm" label-width="100px">
        <el-form-item label="IP">
          <el-input v-model="rtspForm.ip" />
        </el-form-item>
        <el-form-item label="账号">
          <el-input v-model="rtspForm.user" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="rtspForm.pwd" type="password" />
        </el-form-item>
        <el-form-item label="端口">
          <el-input v-model="rtspForm.port" />
        </el-form-item>
      </el-form>
      <el-button @click="autoDetectRtsp" :loading="rtspDetecting">检测</el-button>
      <div v-if="rtspList.length" style="margin-top: 12px;">
        <div v-for="item in rtspList" :key="item.uri" style="margin-bottom: 6px;">
          <el-link @click="selectRtsp(item.uri)">{{ item.profile }}: {{ item.uri }}</el-link>
        </div>
      </div>
      <template #footer>
        <el-button @click="rtspDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
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
  { name: '摄像头与模型绑定', path: '' }
];

const bindList = ref<any[]>([]);
const loading = ref(false);
const dialogVisible = ref(false);
const dialogMode = ref<'add'|'edit'>('add');
const form = ref<any>({ cameraId: '', cameraName: '', cameraSource: '', modelName: '' });
const rtspDialogVisible = ref(false);
const rtspForm = ref<any>({ ip: '', user: '', pwd: '', port: '' });
const rtspList = ref<any[]>([]);
const rtspDetecting = ref(false);

const fetchBindList = async () => {
  loading.value = true;
  try {
    bindList.value = await yoloApi.getCameraModelBindList();
  } catch (e: any) {
    ElMessage.error(e?.response?.data?.info || e?.message || '获取绑定列表失败');
  } finally {
    loading.value = false;
  }
};

const openAddDialog = () => {
  dialogMode.value = 'add';
  form.value = { cameraId: '', cameraName: '', cameraSource: '', modelName: '' };
  dialogVisible.value = true;
};

const openEditDialog = (row: any) => {
  dialogMode.value = 'edit';
  form.value = { ...row };
  dialogVisible.value = true;
};

const submitForm = async () => {
  try {
    if (dialogMode.value === 'add') {
      const res = await yoloApi.addCameraModelBind(form.value);
      ElMessage.success(res?.info || '新增成功');
    } else {
      const res = await yoloApi.updateCameraModelBind(form.value);
      ElMessage.success(res?.info || '修改成功');
    }
    dialogVisible.value = false;
    fetchBindList();
  } catch (e: any) {
    ElMessage.error(e?.response?.data?.info || e?.message || '操作失败');
  }
};

const deleteBind = async (id: number) => {
  try {
    const res = await yoloApi.deleteCameraModelBind(id);
    ElMessage.success(res?.info || '删除成功');
    fetchBindList();
  } catch (e: any) {
    ElMessage.error(e?.response?.data?.info || e?.message || '删除失败');
  }
};

const detectBind = async (id: number) => {
  try {
    const res = await yoloApi.detectCameraModelBind(id);
    ElMessage.success(res?.info || res?.message || '检测已发起');
  } catch (e: any) {
    ElMessage.error(e?.response?.data?.info || e?.message || '检测失败');
  }
};

const openRtspDialog = () => {
  rtspDialogVisible.value = true;
  rtspForm.value = { ip: '', user: '', pwd: '', port: '' };
  rtspList.value = [];
};

const autoDetectRtsp = async () => {
  rtspDetecting.value = true;
  try {
    const res = await yoloApi.autoDetectRtsp(rtspForm.value);
    if (res.success) {
      rtspList.value = res.rtsp_list || [];
      ElMessage.success(res?.info || '检测成功');
    } else {
      ElMessage.error(res?.error || res?.info || '检测失败');
    }
  } catch (e: any) {
    ElMessage.error(e?.response?.data?.info || e?.message || '检测失败');
  } finally {
    rtspDetecting.value = false;
  }
};

const selectRtsp = (uri: string) => {
  form.value.cameraSource = uri;
  rtspDialogVisible.value = false;
};

onMounted(() => {
  fetchBindList();
  setInterval(() => {
    currentTime.value = new Date().toLocaleString();
  }, 1000);
});
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
  max-width: 900px;
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
}
.card:hover {
  box-shadow: 0 8px 32px rgba(0,0,0,0.12);
  border-color: #409eff;
}
</style> 