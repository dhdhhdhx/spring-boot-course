<template>
  <div class="pie-chart">
    <div class="chart-header">
      <h3>{{ title }}</h3>
      <div class="chart-info">
        <span class="total-count">总计: {{ totalCount }}</span>
      </div>
    </div>
    <div
      ref="chartRef"
      class="chart-container"
      :class="{ 'chart-loading': loading }"
    ></div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { useDeviceStore } from '@/stores/device'
import type { EChartsOption } from 'echarts'

interface Props {
  title: string
  height?: string
}

const props = withDefaults(defineProps<Props>(), {
  height: '300px'
})

const deviceStore = useDeviceStore()
const chartRef = ref<HTMLElement>()
let chartInstance: echarts.ECharts | null = null

// 设备状态映射
const statusMap = {
  'ONLINE': { name: '在线', color: '#00ff88' },
  'OFFLINE': { name: '离线', color: '#666666' },
  'WARNING': { name: '警告', color: '#faad14' },
  'ERROR': { name: '错误', color: '#ff4d4f' },
  'MAINTENANCE': { name: '维护', color: '#1890ff' }
}

// 计算饼图数据
const pieData = computed(() => {
  if (!deviceStore.statistics?.statusCount) return []

  // 确保所有可能的状态都显示，即使数量为0
  const allStatuses = ['ONLINE', 'OFFLINE', 'WARNING', 'ERROR', 'MAINTENANCE']

  return allStatuses.map(status => {
    const count = deviceStore.statistics!.statusCount[status] || 0
    const config = statusMap[status as keyof typeof statusMap]
    return {
      name: config?.name || status,
      value: count,
      itemStyle: {
        color: config?.color || '#999999'
      },
      status: status
    }
  })
})

// 总数
const totalCount = computed(() => {
  return pieData.value.reduce((sum, item) => sum + item.value, 0)
})

// 加载状态
const loading = computed(() => deviceStore.loading)

// 图表配置
const chartOption = computed<EChartsOption>(() => ({
  backgroundColor: 'transparent',
  tooltip: {
    trigger: 'item',
    backgroundColor: 'rgba(0, 0, 0, 0.8)',
    borderColor: '#00ff88',
    borderWidth: 1,
    textStyle: {
      color: '#fff'
    },
    formatter: (params: any) => {
      const percent = totalCount.value > 0
        ? ((params.value / totalCount.value) * 100).toFixed(1)
        : '0.0'
      return `
        <div style="font-weight: bold; margin-bottom: 5px;">${params.name}</div>
        <div>数量: ${params.value}</div>
        <div>占比: ${percent}%</div>
      `
    }
  },
  legend: {
    orient: 'vertical',
    right: '10%',
    top: 'center',
    textStyle: {
      color: 'rgba(255, 255, 255, 0.8)',
      fontSize: 12
    },
    itemGap: 12,
    formatter: (name: string) => {
      const item = pieData.value.find(d => d.name === name)
      const percent = item && totalCount.value > 0
        ? ((item.value / totalCount.value) * 100).toFixed(1)
        : '0.0'
      return `${name} ${percent}%`
    }
  },
  series: [
    {
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['35%', '50%'],
      data: pieData.value,
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      },
      label: {
        show: false
      },
      labelLine: {
        show: false
      },
      animationType: 'scale',
      animationEasing: 'elasticOut',
      animationDelay: (idx: number) => Math.random() * 200
    }
  ]
}))

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return

  chartInstance = echarts.init(chartRef.value)
  chartInstance.setOption(chartOption.value)

  // 响应式处理
  const resizeHandler = () => {
    chartInstance?.resize()
  }
  window.addEventListener('resize', resizeHandler)

  // 清理函数
  onUnmounted(() => {
    window.removeEventListener('resize', resizeHandler)
    chartInstance?.dispose()
  })
}

// 更新图表
const updateChart = () => {
  if (chartInstance && chartOption.value) {
    chartInstance.setOption(chartOption.value, true)
  }
}

// 监听数据变化
watch(chartOption, () => {
  updateChart()
}, { deep: true })

// 监听加载状态
watch(loading, (isLoading) => {
  if (chartInstance) {
    if (isLoading) {
      chartInstance.showLoading({
        text: '加载中...',
        color: '#00ff88',
        textColor: '#fff',
        maskColor: 'rgba(0, 0, 0, 0.3)'
      })
    } else {
      chartInstance.hideLoading()
    }
  }
})

onMounted(() => {
  nextTick(() => {
    initChart()
  })
})
</script>

<style scoped>
.pie-chart {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 0 4px;
}

.chart-header h3 {
  font-size: 16px;
  font-weight: 500;
  color: #fff;
  margin: 0;
}

.chart-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.total-count {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
  background: rgba(0, 255, 136, 0.1);
  padding: 4px 8px;
  border-radius: 4px;
  border: 1px solid rgba(0, 255, 136, 0.3);
}

.chart-container {
  flex: 1;
  position: relative;
  min-height: 200px;
}

.chart-loading {
  opacity: 0.6;
}

.chart-container:after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, transparent, rgba(0, 255, 136, 0.1), transparent);
  animation: shimmer 2s infinite;
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.3s;
}

.chart-loading:after {
  opacity: 1;
}

@keyframes shimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}
</style>