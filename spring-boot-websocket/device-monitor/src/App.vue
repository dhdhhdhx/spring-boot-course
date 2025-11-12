<template>
  <div class="dashboard">
    <!-- 顶部标题栏 -->
    <header class="dashboard-header">
      <div class="header-left">
        <h1 class="dashboard-title">设备监控大屏</h1>
        <div class="header-time">
          {{ currentTime }}
        </div>
      </div>
      <div class="header-right">
        <div class="connection-status">
          <el-icon :size="16" :color="wsStatusColor">
            <component :is="wsStatusIcon" />
          </el-icon>
          <span>{{ wsStatusText }}</span>
        </div>
        <el-button size="small" @click="refreshData" :loading="refreshing">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </header>

    <!-- 主要内容区域 -->
    <main class="dashboard-main">
      <!-- 状态卡片区域 -->
      <section class="status-section">
        <el-row :gutter="20">
          <el-col :xs="12" :sm="12" :md="12" :lg="4" :xl="4">
            <StatusCard
              title="设备总数"
              subtitle="所有注册设备"
              :value="deviceStore.statistics?.totalDevices || 0"
              unit="台"
              status-type="normal"
              :icon-component="Monitor"
              :last-update-time="lastUpdateTime"
            />
          </el-col>
          <el-col :xs="12" :sm="12" :md="12" :lg="4" :xl="4">
            <StatusCard
              title="在线设备"
              subtitle="正常运行中"
              :value="deviceStore.statistics?.onlineDevices || 0"
              unit="台"
              status-type="online"
              :icon-component="CircleCheck"
              :last-update-time="lastUpdateTime"
              :trend="getTrend('online')"
              :trend-value="12"
            />
          </el-col>
          <el-col :xs="12" :sm="12" :md="12" :lg="4" :xl="4">
            <StatusCard
              title="离线设备"
              subtitle="连接中断"
              :value="deviceStore.statistics?.offlineDevices || 0"
              unit="台"
              status-type="offline"
              :icon-component="VideoPlay"
              :last-update-time="lastUpdateTime"
            />
          </el-col>
          <el-col :xs="12" :sm="12" :md="12" :lg="4" :xl="4">
            <StatusCard
              title="告警设备"
              subtitle="需要关注"
              :value="deviceStore.statistics?.warningDevices || 0"
              unit="台"
              status-type="warning"
              :icon-component="Warning"
              :last-update-time="lastUpdateTime"
              :trend="getTrend('warning')"
              :trend-value="-5"
            />
          </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="8" :xl="8">
            <StatusCard
              title="今日告警"
              subtitle="累计告警次数"
              :value="deviceStore.alerts.length"
              unit="次"
              status-type="error"
              :icon-component="Bell"
              :last-update-time="lastUpdateTime"
            />
          </el-col>
        </el-row>
      </section>

      <!-- 图表和告警区域 -->
      <section class="content-section">
        <el-row :gutter="20">
          <!-- 左侧图表区域 -->
          <el-col :xs="24" :lg="16">
            <el-row :gutter="20">
              <!-- 温度趋势图 -->
              <el-col :xs="24" :md="12">
                <div class="chart-card">
                  <TrendChart
                    title="温度趋势"
                    metric-type="temperature"
                    height="300px"
                  />
                </div>
              </el-col>
              <!-- 湿度趋势图 -->
              <el-col :xs="24" :md="12">
                <div class="chart-card">
                  <TrendChart
                    title="湿度趋势"
                    metric-type="humidity"
                    height="300px"
                  />
                </div>
              </el-col>
            </el-row>

            <!-- 设备状态饼图 -->
            <div class="chart-card">
              <PieChart
                title="设备状态分布"
                height="300px"
              />
            </div>
          </el-col>

          <!-- 右侧告警面板 -->
          <el-col :xs="24" :lg="8">
            <AlertPanel />
          </el-col>
        </el-row>
      </section>
    </main>

    <!-- 底部信息栏 -->
    <footer class="dashboard-footer">
      <div class="footer-left">
        <span>系统运行时间: {{ systemUptime }}</span>
      </div>
      <div class="footer-right">
        <span>数据更新间隔: 5秒</span>
        <span class="separator">|</span>
        <span>最后更新: {{ lastUpdateTime }}</span>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Monitor,
  CircleCheck,
  Warning,
  Bell,
  Refresh,
  Connection,
  Close,
  WarningFilled,
  VideoPlay
} from '@element-plus/icons-vue'
import { useDeviceStore } from '@/stores/device'
import { useWebSocket } from '@/composables/useWebSocket'
import StatusCard from '@/components/StatusCard.vue'
import TrendChart from '@/components/charts/TrendChart.vue'
import PieChart from '@/components/charts/PieChart.vue'
import AlertPanel from '@/components/AlertPanel.vue'
import dayjs from 'dayjs'

const deviceStore = useDeviceStore()
const { connect, disconnect } = useWebSocket()

// 响应式数据
const currentTime = ref('')
const lastUpdateTime = ref('')
const refreshing = ref(false)
const systemStartTime = dayjs()

// 定时器
let timeTimer: NodeJS.Timeout | null = null
let dataRefreshTimer: NodeJS.Timeout | null = null

// 计算属性
const wsStatusColor = computed(() => {
  return deviceStore.wsConnected ? '#00ff88' : '#ff4d4f'
})

const wsStatusIcon = computed(() => {
  return deviceStore.wsConnected ? Connection : Close
})

const wsStatusText = computed(() => {
  return deviceStore.wsConnected ? '已连接' : '连接断开'
})

const systemUptime = computed(() => {
  const duration = dayjs().diff(systemStartTime)
  const hours = Math.floor(duration / (1000 * 60 * 60))
  const minutes = Math.floor((duration % (1000 * 60 * 60)) / (1000 * 60))
  return `${hours}小时${minutes}分钟`
})

// 获取趋势
const getTrend = (type: string): 'up' | 'down' | 'none' => {
  // 这里可以根据实际数据计算趋势
  const trends = {
    'online': 'up',
    'warning': 'down'
  }
  return trends[type as keyof typeof trends] || 'none'
}

// 更新当前时间
const updateCurrentTime = () => {
  currentTime.value = dayjs().format('YYYY-MM-DD HH:mm:ss')
}

// 刷新数据
const refreshData = async () => {
  refreshing.value = true
  try {
    await deviceStore.initData()
    lastUpdateTime.value = dayjs().format('HH:mm:ss')
    ElMessage.success('数据刷新成功')
  } catch (error) {
    ElMessage.error('数据刷新失败')
  } finally {
    refreshing.value = false
  }
}

// 自动刷新数据
const autoRefreshData = async () => {
  try {
    await deviceStore.fetchLatestData()
    await deviceStore.fetchStatistics()
    lastUpdateTime.value = dayjs().format('HH:mm:ss')
  } catch (error) {
    console.error('自动刷新数据失败:', error)
  }
}

// 初始化
const init = async () => {
  // 连接WebSocket
  connect()

  // 初始化数据
  await refreshData()

  // 启动定时器
  timeTimer = setInterval(updateCurrentTime, 1000)
  dataRefreshTimer = setInterval(autoRefreshData, 5000) // 5秒自动刷新

  updateCurrentTime()
}

// 清理
const cleanup = () => {
  if (timeTimer) {
    clearInterval(timeTimer)
    timeTimer = null
  }
  if (dataRefreshTimer) {
    clearInterval(dataRefreshTimer)
    dataRefreshTimer = null
  }
  disconnect()
}

onMounted(() => {
  init()
})

onUnmounted(() => {
  cleanup()
})
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  background: linear-gradient(135deg, #0f1419 0%, #1a1f2e 100%);
  color: #fff;
  display: flex;
  flex-direction: column;
}

/* 顶部标题栏 */
.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: rgba(0, 0, 0, 0.3);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.header-left {
  display: flex;
  align-items: baseline;
  gap: 24px;
}

.dashboard-title {
  font-size: 24px;
  font-weight: 600;
  background: linear-gradient(90deg, #00ff88, #1890ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
}

.header-time {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  font-family: 'Monaco', 'Menlo', monospace;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.connection-status {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
}

/* 主要内容区域 */
.dashboard-main {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

.status-section {
  margin-bottom: 24px;
}

.chart-card {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.chart-card:hover {
  border-color: rgba(0, 255, 136, 0.3);
  box-shadow: 0 8px 24px rgba(0, 255, 136, 0.1);
}

.content-section {
  margin-bottom: 24px;
}

/* 底部信息栏 */
.dashboard-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: rgba(0, 0, 0, 0.3);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
}

.footer-left,
.footer-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.separator {
  color: rgba(255, 255, 255, 0.3);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .dashboard-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }

  .header-left {
    flex-direction: column;
    gap: 8px;
  }

  .dashboard-title {
    font-size: 20px;
  }

  .dashboard-main {
    padding: 16px;
  }

  .dashboard-footer {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
}

/* 大屏幕下的样式优化 */
@media (min-width: 1200px) {
  .status-section {
    margin-bottom: 32px;
  }
}

/* 超大屏幕下的样式优化 */
@media (min-width: 1600px) {
  .status-section {
    margin-bottom: 40px;
  }
}

@media (max-width: 480px) {
  .dashboard-header {
    padding: 16px;
  }

  .dashboard-main {
    padding: 12px;
  }

  .dashboard-footer {
    padding: 12px 16px;
  }
}

/* Element Plus 样式覆盖 */
:deep(.el-row) {
  margin-right: -10px !important;
  margin-left: -10px !important;
}

:deep(.el-col) {
  padding-right: 10px !important;
  padding-left: 10px !important;
}

:deep(.el-button--small) {
  padding: 6px 12px;
  font-size: 12px;
}

:deep(.el-button) {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.2);
  color: #fff;
}

:deep(.el-button:hover) {
  background: rgba(0, 255, 136, 0.2);
  border-color: rgba(0, 255, 136, 0.4);
}
</style>