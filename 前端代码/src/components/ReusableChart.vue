<!--
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-04-18 13:39:23
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-04-19 22:36:04
 * @FilePath: \front\src\components\ReusableChart.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
  <div ref="chartRef" :style="{ width: '100%', height: '400px' }"></div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import * as echarts from 'echarts';

const props = defineProps({
  chartData: { type: Object, required: true },
  chartType: { type: String, default: 'line' }
});

const chartRef = ref(null);
let chart = null;

const initChart = () => {
  if (chartRef.value) {
    chart = echarts.init(chartRef.value, 'dark');
    updateChart();
  }
};

const updateChart = () => {
  if (chart) {
    const option = {
      xAxis: {
        type: 'category',
        data: props.chartData.xAxisData
      },
      yAxis: {
        type: 'value'
      },
      series: [{
        data: props.chartData.yAxisData,
        type: props.chartType
      }]
    };
    chart.setOption(option);
  }
};

onMounted(() => {
  initChart();
});

watch(() => props.chartData, () => {
  updateChart();
}, { deep: true });
</script>

<style scoped>
</style>