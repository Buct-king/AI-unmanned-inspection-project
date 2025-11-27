<template>
  <div class="alarm-process dark-theme">
    <CommonHeader :currentTime="currentTime" :theme="theme" @toggleTheme="toggleTheme" />
    <div class="main-content">
      <CommonSidebar :isCollapse="isCollapse" />
      <div class="main">
        <CommonBreadcrumb :paths="[
          {name: '首页', path: '/'}, 
          {name: '报警管理', path: '/alarm-management'},
          {name: '报警处理', path: ''}
        ]" />
        
        <div class="content-section">
          <div class="process-layout">
            <!-- 报警信息 -->
            <div class="alarm-info">
              <div class="info-header">
                <h2>报警详情</h2>
                <el-tag :type="getAlarmLevelType(alarmInfo.level)" size="large">
                  {{ getAlarmLevelText(alarmInfo.level) }}
                </el-tag>
              </div>
              
              <div class="info-content">
                <div class="info-item">
                  <span class="label">设备名称：</span>
                  <span class="value">{{ alarmInfo.deviceName }}</span>
                </div>
                <div class="info-item">
                  <span class="label">报警类型：</span>
                  <span class="value">{{ alarmInfo.type }}</span>
                </div>
                <div class="info-item">
                  <span class="label">报警时间：</span>
                  <span class="value">{{ alarmInfo.time }}</span>
                </div>
                <div class="info-item">
                  <span class="label">报警描述：</span>
                  <span class="value">{{ alarmInfo.description }}</span>
                </div>
                <div class="info-item">
                  <span class="label">当前状态：</span>
                  <el-tag :type="getStatusType(alarmInfo.status)">{{ alarmInfo.status }}</el-tag>
                </div>
              </div>
            </div>

            <!-- 处理表单 -->
            <div class="process-form">
              <h3>处理流程</h3>
              <el-form ref="formRef" :model="processForm" label-width="120px">
                <el-form-item label="问题分析" required>
                  <el-input
                    v-model="processForm.analysis"
                    type="textarea"
                    rows="3"
                    placeholder="请详细描述问题原因分析..."
                  />
                </el-form-item>

                <el-form-item label="处理建议" required>
                  <el-input
                    v-model="processForm.suggestion"
                    type="textarea"
                    rows="3"
                    placeholder="请输入现场处理建议..."
                  />
                </el-form-item>

                <el-form-item label="处理优先级">
                  <el-select v-model="processForm.priority" placeholder="请选择处理优先级">
                    <el-option label="紧急" value="urgent" />
                    <el-option label="高" value="high" />
                    <el-option label="中" value="medium" />
                    <el-option label="低" value="low" />
                  </el-select>
                </el-form-item>

                <el-form-item label="预计处理时间">
                  <el-input-number 
                    v-model="processForm.estimatedTime" 
                    :min="1"
                    :max="72"
                  />
                  <span class="unit-text">小时</span>
                </el-form-item>

                <el-form-item label="需要停机">
                  <el-switch v-model="processForm.needShutdown" />
                </el-form-item>

                <el-form-item label="维修人员">
                  <el-select
                    v-model="processForm.maintainers"
                    multiple
                    placeholder="请选择维修人员"
                  >
                    <el-option
                      v-for="person in maintainersList"
                      :key="person.id"
                      :label="person.name"
                      :value="person.id"
                    />
                  </el-select>
                </el-form-item>

                <el-form-item label="所需备件">
                  <el-select
                    v-model="processForm.spareParts"
                    multiple
                    filterable
                    allow-create
                    placeholder="请选择或输入所需备件"
                  >
                    <el-option
                      v-for="part in sparePartsList"
                      :key="part.id"
                      :label="part.name"
                      :value="part.id"
                    />
                  </el-select>
                </el-form-item>

                <el-form-item label="预防措施" required>
                  <el-input
                    v-model="processForm.prevention"
                    type="textarea"
                    rows="3"
                    placeholder="请输入预防措施建议..."
                  />
                </el-form-item>

                <el-form-item label="附件">
                  <el-upload
                    action="#"
                    list-type="picture-card"
                    :auto-upload="false"
                    :on-change="handleFileChange"
                  >
                    <el-icon><Plus /></el-icon>
                  </el-upload>
                </el-form-item>

                <el-form-item label="备注">
                  <el-input
                    v-model="processForm.remarks"
                    type="textarea"
                    rows="2"
                    placeholder="其他补充说明..."
                  />
                </el-form-item>
              </el-form>

              <div class="form-actions">
                <el-button type="primary" @click="submitProcess">提交处理方案</el-button>
                <el-button @click="goBack">返回</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Plus } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import CommonHeader from '@/components/CommonHeader.vue';
import CommonSidebar from '@/components/CommonSidebar.vue';
import CommonBreadcrumb from '@/components/CommonBreadcrumb.vue';

const route = useRoute();
const router = useRouter();
const currentTime = ref(new Date().toLocaleString());
const theme = ref('dark');
const isCollapse = ref(false);

// 报警信息
const alarmInfo = ref({
  id: route.params.id,
  deviceName: '设备1',
  type: '温度异常',
  level: 'error',
  time: '2024-04-19 10:30:00',
  description: '设备温度超过阈值（85℃）',
  status: '处理中'
});

// 处理表单
const processForm = ref({
  analysis: '',
  suggestion: '',
  priority: '',
  estimatedTime: 2,
  needShutdown: false,
  maintainers: [],
  spareParts: [],
  prevention: '',
  attachments: [],
  remarks: ''
});

// 模拟数据
const maintainersList = ref([
  { id: 1, name: '张工' },
  { id: 2, name: '李工' },
  { id: 3, name: '王工' }
]);

const sparePartsList = ref([
  { id: 1, name: '温度传感器' },
  { id: 2, name: '控制板' },
  { id: 3, name: '散热风扇' }
]);

// 工具函数
const getAlarmLevelType = (level: string) => {
  const types: Record<string, string> = {
    error: 'danger',
    warning: 'warning',
    info: 'info'
  };
  return types[level] || 'info';
};

const getAlarmLevelText = (level: string) => {
  const texts: Record<string, string> = {
    error: '严重',
    warning: '警告',
    info: '提示'
  };
  return texts[level] || '未知';
};

const getStatusType = (status: string) => {
  const types: Record<string, string> = {
    '待处理': 'danger',
    '处理中': 'warning',
    '已处理': 'success'
  };
  return types[status] || 'info';
};

// 处理文件上传
const handleFileChange = (file: any) => {
  processForm.value.attachments.push(file);
};

// 提交处理
const submitProcess = async () => {
  try {
    // TODO: 调用API提交处理方案
    ElMessage.success('处理方案已提交');
    router.push('/alarm-management');
  } catch (error) {
    ElMessage.error('提交失败，请重试');
  }
};

const goBack = () => {
  router.back();
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

onMounted(() => {
  // TODO: 根据报警ID获取详细信息
});
</script>

<style scoped>
.alarm-process {
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

.process-layout {
  display: grid;
  gap: 24px;
}

.alarm-info {
  background-color: #1a1c1e;
  border-radius: 8px;
  padding: 24px;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.info-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 500;
  color: #fff;
}

.info-content {
  display: grid;
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
}

.info-item .label {
  color: #8c8c8c;
  width: 100px;
}

.info-item .value {
  color: #fff;
}

.process-form {
  background-color: #1a1c1e;
  border-radius: 8px;
  padding: 24px;
}

.process-form h3 {
  margin: 0 0 24px 0;
  font-size: 18px;
  font-weight: 500;
  color: #fff;
}

:deep(.el-form-item__label) {
  color: #8c8c8c !important;
}

:deep(.el-input__inner),
:deep(.el-textarea__inner) {
  background-color: #282b30 !important;
  border-color: #303030 !important;
  color: #fff !important;
}

:deep(.el-select .el-input__inner) {
  background-color: #282b30 !important;
}

.unit-text {
  margin-left: 8px;
  color: #8c8c8c;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 24px;
}

:deep(.el-upload--picture-card) {
  background-color: #282b30 !important;
  border-color: #303030 !important;
  color: #8c8c8c !important;
}

:deep(.el-upload-list--picture-card .el-upload-list__item) {
  background-color: #282b30 !important;
  border-color: #303030 !important;
}
</style> 