<template>
  <div class="system-management dark-theme">
    <CommonHeader :currentTime="currentTime" :theme="theme" @toggleTheme="toggleTheme" />
    <div class="main-content">
      <CommonSidebar :isCollapse="isCollapse" />
      <div class="main">
        <CommonBreadcrumb :paths="[{name: '首页', path: '/'}, {name: '系统管理', path: '/system-management'}]" />
        
        <div class="content-section">
          <div class="section-header">
            <h3>用户管理</h3>
            <div class="operation-buttons">
              <el-button type="primary" @click="addUser">
                <el-icon><Plus /></el-icon>新增用户
              </el-button>
              <!-- <el-button @click="batchDelete" :disabled="!selectedUsers.length">
                <el-icon><Delete /></el-icon>批量删除
              </el-button> -->
            </div>
          </div>

          <div class="filter-section">
            <div class="filter-items">
              <el-input
                v-model="searchKeyword"
                placeholder="输入用户名搜索"
                class="filter-item"
                clearable
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <el-select 
                v-model="roleFilter" 
                placeholder="角色" 
                class="filter-item" 
                clearable
              >
                <el-option label="全部" :value="-1" />
                <!-- 管理员 -->
                <el-option label="管理员" :value="1" />
                <!-- 普通用户 -->
                <el-option label="普通用户" :value="0" />
              </el-select>
              <el-select 
                v-model="statusFilter" 
                placeholder="状态" 
                class="filter-item" 
                clearable
              >
                <el-option label="全部" :value="-1" />
                <el-option label="启用" :value="1" />
                <el-option label="禁用" :value="0" />
              </el-select>
            </div>
            <div class="filter-buttons">
              <el-button type="primary" @click="query">查询</el-button>
              <el-button @click="reset">重置</el-button>
            </div>
          </div>

          <el-table 
            :data="userData" 
            style="width: 100%"
            class="custom-table"
            @selection-change="handleSelectionChange"
          >
            <el-table-column 
              type="selection" 
              width="55" 
            />
            <el-table-column 
              type="index" 
              label="序号" 
              width="80" 
            />
            <el-table-column 
              prop="userId" 
              label="用户ID" 
              width="100"
            />
            <el-table-column 
              prop="name" 
              label="姓名" 
            />
            <el-table-column 
              prop="phoneNumber" 
              label="手机号" 
            />
            <el-table-column 
              prop="role" 
              label="角色"
            >
              <template #default="{ row }">
                <el-tag :type="getRoleType(row.role)">{{ row.role }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="row.status === '启用' ? 'success' : 'info'">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="250">
              <template #default="{ row }">
                <el-button type="primary" link @click="editUser(row)">编辑</el-button>
                <el-button type="primary" link @click="changeRole(row)">权限</el-button>
                <el-button 
                  type="primary" 
                  link 
                  @click="toggleUserStatus(row)"
                >
                  {{ row.status === '启用' ? '禁用' : '启用' }}
                </el-button>
                <!-- <el-button type="danger" link @click="deleteUser(row)">删除</el-button> -->
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-container">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="total"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 用户表单对话框 -->
    <el-dialog
      v-model="userDialogVisible"
      :title="dialogType === 'add' ? '新增用户' : '编辑用户'"
      width="500px"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userRules"
        label-width="100px"
      >
        <el-form-item label="姓名" prop="name">
          <el-input v-model="userForm.name" />
        </el-form-item>
        <el-form-item label="手机号" prop="phoneNumber">
          <el-input v-model="userForm.phoneNumber" :disabled="dialogType === 'edit'" />
        </el-form-item>
        <el-form-item label="角色" prop="roleType">
          <el-select v-model="userForm.roleType" placeholder="请选择角色">
            <el-option label="普通用户" :value="0" />
            <el-option label="管理员" :value="1" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="userDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitUserForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 权限设置对话框 -->
    <el-dialog
      v-model="roleDialogVisible"
      title="权限设置"
      width="500px"
    >
      <el-form
        ref="roleFormRef"
        :model="roleForm"
        label-width="100px"
      >
        <el-form-item label="用户角色" prop="role">
          <el-select v-model="roleForm.role" placeholder="请选择角色">
            <el-option :label="'管理员'" :value="1" />
            <el-option :label="'普通用户'" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="roleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitRoleForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, onBeforeUnmount } from 'vue';
import { Plus, Delete, Search } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import CommonHeader from '@/components/CommonHeader.vue';
import CommonSidebar from '@/components/CommonSidebar.vue';
import CommonBreadcrumb from '@/components/CommonBreadcrumb.vue';
import { userApi, systemApi } from '@/api';

const currentTime = ref(new Date().toLocaleString());
const theme = ref('dark');
const isCollapse = ref(false);

const searchKeyword = ref('');
const roleFilter = ref(-1);  // -1 表示全部
const statusFilter = ref(-1); // -1 表示全部
const selectedUsers = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(100);

const userData = ref([]);  // 清空初始数据，改为从API获取

const getRoleType = (role) => {
  const types = {
    '管理员': 'danger',
    '普通用户': 'info'
  };
  return types[role] || 'info';
};

const userDialogVisible = ref(false);
const roleDialogVisible = ref(false);
const dialogType = ref('add'); // 'add' 或 'edit'
const userFormRef = ref(null);
const roleFormRef = ref(null);
const permissionTree = ref(null);

const userForm = reactive({
  userId: '',
  name: '',
  phoneNumber: '',
  password: '',
  confirmPassword: '',
  roleType: 0  // 默认为普通用户
});

const roleForm = reactive({
  userId: '',
  role: '',
  name: '',
  phoneNumber: '',
  permissions: []
});

const userRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  phoneNumber: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    // { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于 6 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== userForm.password) {
          callback(new Error('两次输入密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
};

const permissionList = [
  {
    id: 1,
    name: '系统管理',
    children: [
      { id: 11, name: '用户管理' },
      { id: 12, name: '角色管理' },
      { id: 13, name: '权限设置' }
    ]
  },
  {
    id: 2,
    name: '设备管理',
    children: [
      { id: 21, name: '设备监控' },
      { id: 22, name: '报警管理' },
      { id: 23, name: '维护记录' }
    ]
  }
];

// 定时器
let dataTimer = null;
const REFRESH_INTERVAL = 30000; // 30秒刷新一次

// 获取用户列表数据
const fetchUserList = async () => {
  try {
    const params = {
      pageNo: currentPage.value,
      pageSize: pageSize.value,
      name: searchKeyword.value || undefined,
      roleType: roleFilter.value === -1 ? undefined : roleFilter.value,
      status: statusFilter.value === -1 ? undefined : statusFilter.value
    };
    
    const res = await userApi.loadList(params);  // 使用 userApi 而不是 systemApi
    if (res.code === 200) {
      userData.value = res.data.list.map(user => {
        console.log('处理用户数据:', user);  // 添加日志
        return {
          ...user,
          role: user.roleType === 1 ? '管理员' : '普通用户',
          status: user.status === 1 ? '启用' : '禁用'
        };
      });
      total.value = res.data.pageTotal;
      currentPage.value = res.data.pageNo;
      pageSize.value = res.data.pageSize;
    } else {
      ElMessage.error(res.info || '获取用户列表失败');
    }
  } catch (error) {
    console.error('获取用户列表失败:', error);
    ElMessage.error('获取用户列表失败');
  }
};

// 开始定时刷新
const startAutoRefresh = () => {
  // 清除可能存在的旧定时器
  stopAutoRefresh();

  // 设置新的定时器
  dataTimer = window.setInterval(fetchUserList, REFRESH_INTERVAL);
};

// 停止定时刷新
const stopAutoRefresh = () => {
  if (dataTimer) {
    clearInterval(dataTimer);
    dataTimer = null;
  }
};

// 查询方法
const query = () => {
  fetchUserList();
};

// 重置方法
const reset = () => {
  searchKeyword.value = '';
  roleFilter.value = -1;
  statusFilter.value = -1;
  currentPage.value = 1;
  pageSize.value = 10;
  query();
};

const addUser = () => {
  dialogType.value = 'add';
  resetUserForm();
  userDialogVisible.value = true;
};

const editUser = (row) => {
  dialogType.value = 'edit';
  resetUserForm();
  Object.assign(userForm, {
    userId: row.userId,
    name: row.name,
    phoneNumber: row.phoneNumber,
    roleType: row.role === '管理员' ? 1 : 0
  });
  userDialogVisible.value = true;
};

const resetUserForm = () => {
  Object.assign(userForm, {
    name: '',
    phoneNumber: '',
    password: '',
    confirmPassword: '',
    roleType: 0
  });
};

const submitUserForm = async () => {
  if (!userFormRef.value) return;
  
  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const userData = {
          name: userForm.name,
          phoneNumber: userForm.phoneNumber,
          roleType: userForm.roleType
        };

        // Only include userId for edit operations
        if (dialogType.value === 'edit' && userForm.userId) {
          userData.userId = userForm.userId;
        }

        const res = await userApi.update(userData);

        if (res.code === 200) {
          ElMessage.success(dialogType.value === 'add' ? '添加用户成功' : '更新用户成功');
          userDialogVisible.value = false;
          query();
        } else {
          ElMessage.error(res.info || '操作失败');
        }
      } catch (error) {
        console.error('操作失败:', error);
        ElMessage.error('操作失败');
      }
    }
  });
};

const changeRole = (row) => {
  roleForm.userId = row.userId;  // 设置用户ID
  roleForm.role = row.role === '管理员' ? 1 : 0;  // 设置当前角色
  roleForm.name = row.name;  // 保存用户名
  roleForm.phoneNumber = row.phoneNumber;  // 保存手机号
  roleDialogVisible.value = true;
};

const submitRoleForm = async () => {
  try {
    const res = await userApi.update({
      userId: roleForm.userId,
      name: roleForm.name,
      phoneNumber: roleForm.phoneNumber,
      roleType: roleForm.role
    });

    if (res.code === 200) {
      ElMessage.success('权限设置成功');
      roleDialogVisible.value = false;
      query();  // 刷新列表
    } else {
      ElMessage.error(res.info || '设置失败');
    }
  } catch (error) {
    console.error('设置失败:', error);
    ElMessage.error('设置失败');
  }
};

const toggleUserStatus = async (row) => {
  try {
    const res = await userApi.update({
      userId: row.userId,
      phoneNumber: row.phoneNumber,
      status: row.status === '启用' ? 0 : 1
    });

    if (res.code === 200) {
      const newStatus = row.status === '启用' ? '禁用' : '启用';
      ElMessage.success(`${newStatus}成功`);
      query();
    } else {
      ElMessage.error(res.info || '操作失败');
    }
  } catch (error) {
    console.error('操作失败:', error);
    ElMessage.error('操作失败');
  }
};

const deleteUser = async (row) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该用户吗？此操作不可恢复',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    
    const res = await userApi.delete(row.phoneNumber);
    if (res.code === 200) {
      ElMessage.success('删除成功');
      query();
    } else {
      ElMessage.error(res.info || '删除失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error);
      ElMessage.error('删除失败');
    }
  }
};

const handleSelectionChange = (val) => {
  selectedUsers.value = val;
};

const handleSizeChange = (val) => {
  pageSize.value = val;
  query();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  query();
};

// 添加计算属性来处理显示值
const roleDisplayValue = computed(() => {
  if (roleFilter.value === -1) return '';
  return roleFilter.value === 1 ? '管理员' : '普通用户';
});

const statusDisplayValue = computed(() => {
  if (statusFilter.value === -1) return '';
  return statusFilter.value === 1 ? '启用' : '禁用';
});

onMounted(() => {
  fetchUserList();
  startAutoRefresh();
});

// 组件卸载时清理定时器
onBeforeUnmount(() => {
  stopAutoRefresh();
});
</script>

<style scoped>
.system-management {
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
  padding: 20px;
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
}

.operation-buttons {
  display: flex;
  gap: 12px;
}

.filter-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  width: 100%;
}

.filter-items {
  display: flex;
  gap: 12px;
}

.filter-buttons {
  display: flex;
  gap: 12px;
}

.filter-item {
  width: 200px;
}

.custom-table {
  margin-bottom: 20px;
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

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
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

:deep(.el-form-item__label) {
  color: #8c8c8c !important;
}

:deep(.el-input__inner),
:deep(.el-select .el-input__inner) {
  background-color: #1a1c1e !important;
  border-color: #303030 !important;
  color: #fff !important;
}

:deep(.el-tree) {
  background-color: #1a1c1e !important;
  color: #fff !important;
}

:deep(.el-tree-node__content:hover) {
  background-color: #282b30 !important;
}

:deep(.el-tree-node.is-current > .el-tree-node__content) {
  background-color: #282b30 !important;
}
</style>