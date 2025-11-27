<template>
  <el-switch
    v-model="isDark"
    inline-prompt
    :active-icon="Moon"
    :inactive-icon="Sunny"
    :active-color="'var(--el-fill-color-dark)'"
    :inactive-color="'var(--el-color-primary)'"
    @change="toggleDark"
  />
</template>

<script setup lang="ts">
import { useDark, useToggle } from '@vueuse/core'
import { Moon, Sunny } from '@element-plus/icons-vue'

const isDark = useDark()
const toggleDark = useToggle(isDark)
</script>

<style scoped>
</style> 