<template>
  <div class="status-card">
    <div class="card-header">
      <div class="card-icon">
        <el-icon :size="24" :color="iconColor">
          <component :is="iconComponent" />
        </el-icon>
      </div>
      <div class="card-status" :class="`status-${statusType}`">
        <div class="status-dot"></div>
        <span>{{ statusText }}</span>
      </div>
    </div>

    <div class="card-content">
      <div class="card-value">
        <span class="value-number" :class="{ 'value-update': isUpdating }">
          {{ formattedValue }}
        </span>
        <span class="value-unit">{{ unit }}</span>
      </div>

      <div class="card-info">
        <div class="info-title">{{ title }}</div>
        <div class="info-subtitle">{{ subtitle }}</div>
      </div>
    </div>

    <div class="card-footer">
      <div class="update-time">
        <el-icon size="12">
          <Clock />
        </el-icon>
        {{ formatTime(lastUpdateTime) }}
      </div>
      <div v-if="trend !== 'none'" class="trend" :class="`trend-${trend}`">
        <el-icon size="12">
          <CaretTop v-if="trend === 'up'" />
          <CaretBottom v-else-if="trend === 'down'" />
          <Minus v-else />
        </el-icon>
        <span>{{ trendValue }}%</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { Clock, CaretTop, CaretBottom, Minus } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

interface Props {
  title: string
  subtitle?: string
  value: number | string
  unit?: string
  statusType: 'online' | 'offline' | 'warning' | 'error' | 'normal'
  iconComponent: any
  lastUpdateTime: string
  trend?: 'up' | 'down' | 'none'
  trendValue?: number
}

const props = withDefaults(defineProps<Props>(), {
  unit: '',
  subtitle: '',
  trend: 'none',
  trendValue: 0
})

// 更新动画状态
const isUpdating = ref(false)

// 格式化值
const formattedValue = computed(() => {
  if (typeof props.value === 'number') {
    return props.value.toLocaleString()
  }
  return props.value
})

// 图标颜色
const iconColor = computed(() => {
  const colorMap = {
    'online': '#00ff88',
    'offline': '#666666',
    'warning': '#faad14',
    'error': '#ff4d4f',
    'normal': '#1890ff'
  }
  return colorMap[props.statusType]
})

// 状态文本
const statusText = computed(() => {
  const textMap = {
    'online': '在线',
    'offline': '离线',
    'warning': '警告',
    'error': '错误',
    'normal': '正常'
  }
  return textMap[props.statusType]
})

// 格式化时间
const formatTime = (time: string) => {
  return dayjs(time).format('HH:mm:ss')
}

// 监听值变化，触发更新动画
watch(() => props.value, (newValue, oldValue) => {
  if (newValue !== oldValue) {
    isUpdating.value = true
    setTimeout(() => {
      isUpdating.value = false
    }, 300)
  }
})
</script>

<style scoped>
.status-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.05), rgba(255, 255, 255, 0.02));
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 20px;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.status-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 255, 136, 0.1);
  border-color: rgba(0, 255, 136, 0.3);
}

.status-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--icon-color), transparent);
  --icon-color: v-bind(iconColor);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.card-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.card-status {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.05);
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.status-online {
  color: #00ff88;
  background: rgba(0, 255, 136, 0.1);
}

.status-offline {
  color: #666666;
  background: rgba(102, 102, 102, 0.1);
}

.status-warning {
  color: #faad14;
  background: rgba(250, 173, 20, 0.1);
}

.status-error {
  color: #ff4d4f;
  background: rgba(255, 77, 79, 0.1);
}

.status-normal {
  color: #1890ff;
  background: rgba(24, 144, 255, 0.1);
}

.card-content {
  margin-bottom: 16px;
}

.card-value {
  display: flex;
  align-items: baseline;
  gap: 4px;
  margin-bottom: 8px;
}

.value-number {
  font-size: 32px;
  font-weight: bold;
  color: #fff;
  line-height: 1;
}

.value-unit {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}

.value-update {
  animation: valueUpdate 0.3s ease-in-out;
}

@keyframes valueUpdate {
  0% { transform: scale(1); }
  50% { transform: scale(1.05); color: #00ff88; }
  100% { transform: scale(1); }
}

.card-info {
  color: rgba(255, 255, 255, 0.7);
}

.info-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 2px;
}

.info-subtitle {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.update-time {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

.trend {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: 12px;
  font-weight: 500;
}

.trend-up {
  color: #00ff88;
}

.trend-down {
  color: #ff4d4f;
}

.trend-none {
  color: rgba(255, 255, 255, 0.5);
}

/* 响应式 */
@media (max-width: 768px) {
  .status-card {
    padding: 16px;
  }

  .card-value {
    margin-bottom: 6px;
  }

  .value-number {
    font-size: 28px;
  }

  .card-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>