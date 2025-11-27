<template>
  <el-breadcrumb class="common-breadcrumb" separator="/">
    <el-breadcrumb-item v-for="(item, index) in breadcrumbItems" :key="index" :to="item.path">
      <span class="breadcrumb-text">{{ item.title }}</span>
    </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();
const breadcrumbItems = ref([]);

// 生成面包屑数据
const generateBreadcrumb = () => {
  const items = [];
  const matched = route.matched;

  // 添加首页
  items.push({
    path: '/',
    title: '首页'
  });

  // 如果当前路径不是首页，添加当前路径
  if (route.path !== '/') {
    matched.forEach(item => {
      if (item.path !== '/') {
        items.push({
          path: item.path,
          title: item.meta?.title || item.name
        });
      }
    });
  }

  return items;
};

// 监听路由变化，更新面包屑
watch(() => route.path, () => {
  breadcrumbItems.value = generateBreadcrumb();
}, { immediate: true });
</script>

<style scoped>
.common-breadcrumb {
  padding: 16px 0;
  background-color: transparent;
}


</style>