<template>
  <div class="device-list">
    <div class="list-header">
      <h3>设备列表</h3>
      <div class="header-controls">
        <el-select v-model="statusFilter" placeholder="筛选状态" clearable size="small">
          <el-option label="全部" value="" />
          <el-option label="在线" value="ONLINE" />
          <el-option label="离线" value="OFFLINE" />
          <el-option label="警告" value="WARNING" />
          <el-option label="错误" value="ERROR" />
          <el-option label="维护" value="MAINTENANCE" />
        </el-select>
      </div>
    </div>

    <div class="list-content">
      <el-table :data="filteredDevices" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="deviceName" label="设备名称" min-width="120">
          <template #default="{ row }">
            <div class="device-name">
              <el-icon size="16" :color="getStatusColor(row.status)">
                <component :is="getStatusIcon(row.status)" />
              </el-icon>
              <span>{{ row.deviceName }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="deviceCode" label="设备编码" width="120" />

        <el-table-column prop="location" label="位置" width="150" />

        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="最新数据" width="150">
          <template #default="{ row }">
            <div class="latest-data" v-if="getLatestDataForDevice(row.id)">
              <div class="data-item">
                <span class="metric-name">温度:</span>
                <span class="metric-value">{{ getLatestMetricValue(row.id, 'temperature') }}°C</span>
              </div>
              <div class="data-item">
                <span class="metric-name">湿度:</span>
                <span class="metric-value">{{ getLatestMetricValue(row.id, 'humidity') }}%</span>
              </div>
            </div>
            <span v-else class="no-data">暂无数据</span>
          </template>
        </el-table-column>

        <el-table-column prop="lastWarningTime" label="最后告警时间" width="160">
          <template #default="{ row }">
            <span v-if="row.lastWarningTime">
              {{ formatTime(row.lastWarningTime) }}
            </span>
            <span v-else class="no-warning">-</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewDeviceDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 设备详情对话框 -->
    <el-dialog v-model="detailVisible" title="设备详情" width="600px">
      <div v-if="selectedDevice" class="device-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="设备名称">
            {{ selectedDevice.deviceName }}
          </el-descriptions-item>
          <el-descriptions-item label="设备编码">
            {{ selectedDevice.deviceCode }}
          </el-descriptions-item>
          <el-descriptions-item label="设备位置">
            {{ selectedDevice.location || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="设备状态">
            <el-tag :type="getStatusTagType(selectedDevice.status)">
              {{ getStatusText(selectedDevice.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ formatTime(selectedDevice.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="更新时间">
            {{ formatTime(selectedDevice.updateTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="最后告警时间" :span="2">
            {{ selectedDevice.lastWarningTime ? formatTime(selectedDevice.lastWarningTime) : '-' }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 设备描述 -->
        <div class="device-description" v-if="selectedDevice.description">
          <h4>设备描述</h4>
          <p>{{ selectedDevice.description }}</p>
        </div>

        <!-- 位置信息 -->
        <div class="device-location" v-if="selectedDevice.longitude && selectedDevice.latitude">
          <h4>设备位置</h4>
          <p>经度: {{ selectedDevice.longitude }}, 纬度: {{ selectedDevice.latitude }}</p>
        </div>

        <!-- 最近数据 -->
        <div class="recent-data">
          <h4>最近数据</h4>
          <el-table :data="getDeviceRecentData(selectedDevice.id)" size="small">
            <el-table-column prop="metricName" label="指标名称">
              <template #default="{ row }">
                {{ getMetricDisplayName(row.metricName) }}
              </template>
            </el-table-column>
            <el-table-column prop="metricValue" label="数值">
              <template #default="{ row }">
                {{ row.metricValue }}{{ row.unit }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="getDataStatusTagType(row.status)" size="small">
                  {{ getDataStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="recordTime" label="记录时间">
              <template #default="{ row }">
                {{ formatTime(row.recordTime) }}
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useDeviceStore } from '@/stores/device'
import dayjs from 'dayjs'
import {
  CircleCheck,
  WarningFilled,
  CircleCloseFilled,
  VideoPlay,
  Tools
} from '@element-plus/icons-vue'
import type { Device } from '@/types'

const deviceStore = useDeviceStore()

// 组件状态
const statusFilter = ref('')
const detailVisible = ref(false)
const selectedDevice = ref<Device | null>(null)

// 计算属性
const devices = computed(() => deviceStore.devices || [])
const loading = computed(() => deviceStore.loading)
const deviceData = computed(() => deviceStore.deviceData || [])

// 过滤后的设备列表
const filteredDevices = computed(() => {
  if (!statusFilter.value) {
    return devices.value
  }
  return devices.value.filter(device => device.status === statusFilter.value)
})

// 获取设备最新数据
const getLatestDataForDevice = (deviceId: number) => {
  return deviceData.value.filter(data => data.deviceId === deviceId)
}

// 获取设备特定指标的最新值
const getLatestMetricValue = (deviceId: number, metricName: string) => {
  const latestData = deviceData.value
    .filter(data => data.deviceId === deviceId && data.metricName === metricName)
    .sort((a, b) => new Date(b.recordTime).getTime() - new Date(a.recordTime).getTime())
  return latestData.length > 0 ? latestData[0].metricValue : '-'
}

// 获取设备最近数据
const getDeviceRecentData = (deviceId: number) => {
  return deviceData.value
    .filter(data => data.deviceId === deviceId)
    .sort((a, b) => new Date(b.recordTime).getTime() - new Date(a.recordTime).getTime())
    .slice(0, 10)
}

// 获取状态颜色
const getStatusColor = (status: string) => {
  const colorMap = {
    'ONLINE': '#00ff88',
    'OFFLINE': '#666666',
    'WARNING': '#faad14',
    'ERROR': '#ff4d4f',
    'MAINTENANCE': '#1890ff'
  }
  return colorMap[status as keyof typeof colorMap] || '#999999'
}

// 获取状态图标
const getStatusIcon = (status: string) => {
  const iconMap = {
    'ONLINE': CircleCheck,
    'OFFLINE': VideoPlay,
    'WARNING': WarningFilled,
    'ERROR': CircleCloseFilled,
    'MAINTENANCE': Tools
  }
  return iconMap[status as keyof typeof iconMap] || CircleCheck
}

// 获取状态标签类型
const getStatusTagType = (status: string) => {
  const typeMap = {
    'ONLINE': 'success',
    'OFFLINE': 'info',
    'WARNING': 'warning',
    'ERROR': 'danger',
    'MAINTENANCE': 'primary'
  }
  return typeMap[status as keyof typeof typeMap] || 'info'
}

// 获取状态文本
const getStatusText = (status: string) => {
  const textMap = {
    'ONLINE': '在线',
    'OFFLINE': '离线',
    'WARNING': '警告',
    'ERROR': '错误',
    'MAINTENANCE': '维护'
  }
  return textMap[status as keyof typeof textMap] || status
}

// 获取数据状态标签类型
const getDataStatusTagType = (status: string) => {
  const typeMap = {
    'normal': 'success',
    'warning': 'warning',
    'high': 'danger',
    'low': 'warning',
    'critical': 'danger'
  }
  return typeMap[status as keyof typeof typeMap] || 'info'
}

// 获取数据状态文本
const getDataStatusText = (status: string) => {
  const textMap = {
    'normal': '正常',
    'warning': '警告',
    'high': '过高',
    'low': '过低',
    'critical': '严重'
  }
  return textMap[status as keyof typeof textMap] || status
}

// 获取指标显示名称
const getMetricDisplayName = (metricName: string) => {
  const nameMap = {
    'temperature': '温度',
    'humidity': '湿度',
    'pressure': '压力',
    'vibration': '振动',
    'power': '功率'
  }
  return nameMap[metricName] || metricName
}

// 格式化时间
const formatTime = (time: string) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

// 查看设备详情
const viewDeviceDetail = (device: Device) => {
  selectedDevice.value = device
  detailVisible.value = true
}

// 初始化
onMounted(async () => {
  if (devices.value.length === 0) {
    await deviceStore.fetchDevices()
  }
})
</script>

<style scoped>
.device-list {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  overflow: hidden;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: rgba(0, 255, 136, 0.05);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.list-header h3 {
  font-size: 16px;
  font-weight: 500;
  color: #fff;
  margin: 0;
}

.header-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

.list-content {
  padding: 16px 20px;
}

.device-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.latest-data {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.data-item {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
}

.metric-name {
  color: rgba(255, 255, 255, 0.7);
}

.metric-value {
  color: #00ff88;
  font-weight: 500;
}

.no-data, .no-warning {
  color: rgba(255, 255, 255, 0.5);
  font-size: 12px;
}

.device-detail {
  color: #fff;
}

.device-description, .device-location, .recent-data {
  margin-top: 20px;
}

.device-description h4,
.device-location h4,
.recent-data h4 {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
  color: #00ff88;
}

.device-description p,
.device-location p {
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.5;
}

:deep(.el-table) {
  background: transparent;
}

:deep(.el-table th) {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.9);
}

:deep(.el-table td) {
  border-color: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.8);
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background: rgba(255, 255, 255, 0.02);
}

:deep(.el-descriptions__label) {
  background: rgba(255, 255, 255, 0.05);
  color: rgba(255, 255, 255, 0.8);
}

:deep(.el-descriptions__content) {
  color: rgba(255, 255, 255, 0.9);
}

:deep(.el-select) {
  --el-select-input-color: rgba(255, 255, 255, 0.8);
  --el-select-input-font-size: 12px;
}

:deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.2);
}
</style>