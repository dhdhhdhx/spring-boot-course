<template>
  <div class="alert-panel">
    <div class="panel-header">
      <h3>实时告警</h3>
      <div class="panel-controls">
        <el-badge :value="alertCount" :hidden="alertCount === 0" class="alert-badge">
          <el-button
            size="small"
            type="danger"
            :disabled="alertCount === 0"
            @click="clearAlerts"
          >
            清除告警
          </el-button>
        </el-badge>
      </div>
    </div>

    <div class="alert-list">
      <el-scrollbar height="400px">
        <div v-if="alerts.length === 0" class="no-alerts">
          <el-empty description="暂无告警信息" :image-size="80">
            <template #image>
              <el-icon size="40" color="#00ff88">
                <Check />
              </el-icon>
            </template>
          </el-empty>
        </div>

        <transition-group v-else name="alert" tag="div">
          <div
            v-for="alert in displayAlerts"
            :key="alert.id"
            class="alert-item"
            :class="[`alert-${alert.level}`, { 'alert-flash': isNewAlert(alert.id) }]"
            @click="viewAlertDetail(alert)"
          >
            <div class="alert-icon">
              <el-icon size="20">
                <Warning v-if="alert.level === 'warning'" />
                <CircleClose v-else-if="alert.level === 'error'" />
                <WarningFilled v-else />
              </el-icon>
            </div>

            <div class="alert-content">
              <div class="alert-title">
                {{ alert.deviceName }} - {{ getMetricDisplayName(alert.metricName) }}
              </div>
              <div class="alert-value">
                {{ alert.value }}{{ getMetricUnit(alert.metricName) }}
              </div>
              <div class="alert-time">
                {{ formatTime(alert.timestamp) }}
              </div>
            </div>

            <div class="alert-status">
              <el-tag
                :type="getTagType(alert.level)"
                size="small"
                effect="dark"
              >
                {{ getLevelText(alert.level) }}
              </el-tag>
            </div>
          </div>
        </transition-group>
      </el-scrollbar>
    </div>

    <!-- 告警详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="告警详情"
      width="500px"
      :before-close="closeDetail"
    >
      <div v-if="selectedAlert" class="alert-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="设备名称">
            {{ selectedAlert.deviceName }}
          </el-descriptions-item>
          <el-descriptions-item label="监控指标">
            {{ getMetricDisplayName(selectedAlert.metricName) }}
          </el-descriptions-item>
          <el-descriptions-item label="当前值">
            <span :class="`value-${selectedAlert.level}`">
              {{ selectedAlert.value }}{{ getMetricUnit(selectedAlert.metricName) }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="告警级别">
            <el-tag :type="getTagType(selectedAlert.level)" effect="dark">
              {{ getLevelText(selectedAlert.level) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="告警时间">
            {{ formatTime(selectedAlert.timestamp) }}
          </el-descriptions-item>
          <el-descriptions-item label="状态描述">
            {{ getStatusDescription(selectedAlert) }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Check, Warning, CircleClose, WarningFilled } from '@element-plus/icons-vue'
import { useDeviceStore } from '@/stores/device'
import dayjs from 'dayjs'
import type { AlertInfo } from '@/types'

const deviceStore = useDeviceStore()

// 组件状态
const detailVisible = ref(false)
const selectedAlert = ref<AlertInfo | null>(null)
const newAlertIds = ref<Set<string>>(new Set())

// 计算属性
const alerts = computed(() => deviceStore.alerts)
const alertCount = computed(() => alerts.value.length)

// 显示的告警列表（限制数量）
const displayAlerts = computed(() => alerts.value.slice(0, 20))

// 检查是否为新告警
const isNewAlert = (alertId: string) => {
  return newAlertIds.value.has(alertId)
}

// 获取指标显示名称
const getMetricDisplayName = (metricName: string) => {
  const nameMap: Record<string, string> = {
    'temperature': '温度',
    'humidity': '湿度',
    'pressure': '压力',
    'vibration': '振动',
    'power': '功率'
  }
  return nameMap[metricName] || metricName
}

// 获取指标单位
const getMetricUnit = (metricName: string) => {
  const unitMap: Record<string, string> = {
    'temperature': '°C',
    'humidity': '%',
    'pressure': 'Pa',
    'vibration': 'Hz',
    'power': 'W'
  }
  return unitMap[metricName] || ''
}

// 获取标签类型
const getTagType = (level: string) => {
  const typeMap: Record<string, string> = {
    'warning': 'warning',
    'error': 'danger',
    'critical': 'danger'
  }
  return typeMap[level] || 'info'
}

// 获取级别文本
const getLevelText = (level: string) => {
  const textMap: Record<string, string> = {
    'warning': '警告',
    'error': '错误',
    'critical': '严重'
  }
  return textMap[level] || level
}

// 格式化时间
const formatTime = (timestamp: string) => {
  return dayjs(timestamp).format('MM-DD HH:mm:ss')
}

// 获取状态描述
const getStatusDescription = (alert: AlertInfo) => {
  const { metricName, value, level } = alert
  const displayName = getMetricDisplayName(metricName)
  const unit = getMetricUnit(metricName)

  if (level === 'critical') {
    return `${displayName}严重异常：${value}${unit}，请立即处理！`
  } else if (level === 'error') {
    return `${displayName}超出正常范围：${value}${unit}，请及时检查。`
  } else {
    return `${displayName}轻微异常：${value}${unit}，建议关注。`
  }
}

// 查看告警详情
const viewAlertDetail = (alert: AlertInfo) => {
  selectedAlert.value = alert
  detailVisible.value = true

  // 从新告警集合中移除
  newAlertIds.value.delete(alert.id)
}

// 关闭详情
const closeDetail = () => {
  detailVisible.value = false
  selectedAlert.value = null
}

// 清除所有告警
const clearAlerts = () => {
  deviceStore.clearAlerts()
  newAlertIds.value.clear()
  ElMessage.success('告警已清除')
}

// 监听新告警
const watchNewAlerts = () => {
  const currentAlertIds = new Set(alerts.value.map(a => a.id))

  // 找出新增的告警
  alerts.value.forEach(alert => {
    if (!newAlertIds.value.has(alert.id)) {
      newAlertIds.value.add(alert.id)

      // 3秒后移除闪烁效果
      setTimeout(() => {
        newAlertIds.value.delete(alert.id)
      }, 3000)
    }
  })
}

// 定时检查新告警
let alertWatcher: NodeJS.Timeout | null = null

onMounted(() => {
  // 启动告警监听
  alertWatcher = setInterval(() => {
    watchNewAlerts()
  }, 1000)
})

onUnmounted(() => {
  if (alertWatcher) {
    clearInterval(alertWatcher)
  }
})
</script>

<style scoped>
.alert-panel {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  overflow: hidden;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: rgba(0, 255, 136, 0.05);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.panel-header h3 {
  font-size: 16px;
  font-weight: 500;
  color: #fff;
  margin: 0;
}

.panel-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

.alert-badge :deep(.el-badge__content) {
  background: #ff4d4f;
  border: 1px solid #ff7875;
}

.alert-list {
  flex: 1;
  padding: 12px;
}

.no-alerts {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  color: rgba(255, 255, 255, 0.5);
}

.alert-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  margin-bottom: 8px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 6px;
  border-left: 3px solid;
  cursor: pointer;
  transition: all 0.3s ease;
}

.alert-item:hover {
  background: rgba(255, 255, 255, 0.08);
  transform: translateX(4px);
}

.alert-warning {
  border-left-color: #faad14;
  background: rgba(250, 173, 20, 0.1);
}

.alert-error {
  border-left-color: #ff4d4f;
  background: rgba(255, 77, 79, 0.1);
}

.alert-critical {
  border-left-color: #ff1a1a;
  background: rgba(255, 26, 26, 0.1);
}

.alert-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

.alert-warning .alert-icon {
  color: #faad14;
}

.alert-error .alert-icon {
  color: #ff4d4f;
}

.alert-critical .alert-icon {
  color: #ff1a1a;
}

.alert-content {
  flex: 1;
}

.alert-title {
  font-size: 14px;
  font-weight: 500;
  color: #fff;
  margin-bottom: 4px;
}

.alert-value {
  font-size: 16px;
  font-weight: bold;
  color: #00ff88;
  margin-bottom: 2px;
}

.alert-time {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

.alert-status {
  display: flex;
  align-items: center;
}

/* 告警动画 */
.alert-enter-active,
.alert-leave-active {
  transition: all 0.3s ease;
}

.alert-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.alert-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

.alert-move {
  transition: transform 0.3s ease;
}

/* 告警详情 */
.alert-detail :deep(.el-descriptions__label) {
  font-weight: 500;
}

.value-warning {
  color: #faad14;
  font-weight: bold;
}

.value-error {
  color: #ff4d4f;
  font-weight: bold;
}

.value-critical {
  color: #ff1a1a;
  font-weight: bold;
}

/* 响应式 */
@media (max-width: 768px) {
  .alert-item {
    padding: 10px 12px;
  }

  .alert-title {
    font-size: 13px;
  }

  .alert-value {
    font-size: 14px;
  }
}
</style>