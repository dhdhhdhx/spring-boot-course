<template>
  <div class="trend-chart">
    <div class="chart-header">
      <h3>{{ title }}</h3>
      <div class="chart-legend">
        <span
          v-for="series in legendData"
          :key="series.name"
          class="legend-item"
        >
          <span
            class="legend-color"
            :style="{ backgroundColor: series.color }"
          ></span>
          {{ series.name }}
        </span>
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
import dayjs from 'dayjs'
import type { EChartsOption } from 'echarts'

interface Props {
  title: string
  metricType: 'temperature' | 'humidity'
  height?: string
}

const props = withDefaults(defineProps<Props>(), {
  height: '300px'
})

const deviceStore = useDeviceStore()
const chartRef = ref<HTMLElement>()
let chartInstance: echarts.ECharts | null = null

// 计算图表数据
const chartData = computed(() => {
  const data = (deviceStore.deviceData || []).filter(
    item => item.metricName === props.metricType
  )

  // 按设备分组
  const groupedData = data.reduce((acc, item) => {
    if (!acc[item.deviceId]) {
      acc[item.deviceId] = []
    }
    acc[item.deviceId].push(item)
    return acc
  }, {} as Record<number, typeof data>)

  // 获取最近的10个时间点
  const allTimestamps = data.map(item => item.recordTime).sort()
  const recentTimestamps = allTimestamps.slice(-10)

  // 为每个设备生成数据系列
  const series = Object.entries(groupedData).map(([deviceId, deviceData]) => {
    const device = (deviceStore.devices || []).find(d => d.id === Number(deviceId))
    const deviceName = device?.deviceName || `设备${deviceId}`

    const values = recentTimestamps.map(timestamp => {
      const dataPoint = deviceData.find(d => d.recordTime === timestamp)
      return dataPoint ? Number(dataPoint.metricValue) : null
    })

    return {
      name: deviceName,
      data: values,
      deviceId: Number(deviceId)
    }
  })

  return {
    timestamps: recentTimestamps.map(time => dayjs(time).format('HH:mm:ss')),
    series: series.filter(s => s.data.some(v => v !== null)) // 过滤掉没有数据的设备
  }
})

// 图例数据
const legendData = computed(() => {
  const colors = ['#00ff88', '#1890ff', '#722ed1', '#fa8c16', '#eb2f96']
  return chartData.value.series.map((series, index) => ({
    name: series.name,
    color: colors[index % colors.length]
  }))
})

// 加载状态
const loading = computed(() => deviceStore.loading)

// 图表配置
const chartOption = computed<EChartsOption>(() => {
  const colors = ['#00ff88', '#1890ff', '#722ed1', '#fa8c16', '#eb2f96']

  return {
    backgroundColor: 'transparent',
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '10%',
      containLabel: true
    },
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(0, 0, 0, 0.8)',
      borderColor: '#00ff88',
      borderWidth: 1,
      textStyle: {
        color: '#fff'
      },
      formatter: (params: any) => {
        let result = `<div style="margin-bottom: 5px; font-weight: bold;">${params[0].axisValue}</div>`
        params.forEach((param: any) => {
          const unit = props.metricType === 'temperature' ? '°C' : '%'
          result += `
            <div style="margin: 2px 0;">
              <span style="display: inline-block; width: 10px; height: 10px; background: ${param.color}; border-radius: 50%; margin-right: 8px;"></span>
              ${param.seriesName}: ${param.value}${unit}
            </div>
          `
        })
        return result
      }
    },
    legend: {
      show: false
    },
    xAxis: {
      type: 'category',
      data: chartData.value.timestamps,
      axisLine: {
        lineStyle: {
          color: 'rgba(255, 255, 255, 0.3)'
        }
      },
      axisLabel: {
        color: 'rgba(255, 255, 255, 0.7)',
        fontSize: 11
      }
    },
    yAxis: {
      type: 'value',
      axisLine: {
        lineStyle: {
          color: 'rgba(255, 255, 255, 0.3)'
        }
      },
      axisLabel: {
        color: 'rgba(255, 255, 255, 0.7)',
        fontSize: 11,
        formatter: (value: number) => {
          const unit = props.metricType === 'temperature' ? '°C' : '%'
          return value + unit
        }
      },
      splitLine: {
        lineStyle: {
          color: 'rgba(255, 255, 255, 0.1)'
        }
      }
    },
    series: chartData.value.series.map((series, index) => ({
      name: series.name,
      type: 'line',
      data: series.data,
      smooth: true,
      symbol: 'circle',
      symbolSize: 4,
      lineStyle: {
        color: colors[index % colors.length],
        width: 2
      },
      itemStyle: {
        color: colors[index % colors.length]
      },
      areaStyle: {
        opacity: 0.1,
        color: colors[index % colors.length]
      }
    }))
  }
})

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
.trend-chart {
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

.chart-legend {
  display: flex;
  gap: 16px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
}

.legend-color {
  width: 8px;
  height: 8px;
  border-radius: 50%;
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