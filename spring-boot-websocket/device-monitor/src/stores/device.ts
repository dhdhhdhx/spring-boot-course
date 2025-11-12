import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Device, DeviceData, DeviceStatistics, AlertInfo } from '@/types'
import { deviceApi } from '@/api'

export const useDeviceStore = defineStore('device', () => {
  // 状态
  const devices = ref<Device[]>([])
  const deviceData = ref<DeviceData[]>([])
  const statistics = ref<DeviceStatistics | null>(null)
  const alerts = ref<AlertInfo[]>([])
  const loading = ref(false)
  const wsConnected = ref(false)

  // 计算属性
  const onlineDevices = computed(() =>
    devices.value.filter(device => device.status === 'ONLINE')
  )

  const warningDevices = computed(() =>
    devices.value.filter(device =>
      device.status === 'WARNING' || device.status === 'ERROR'
    )
  )

  const offlineDevices = computed(() =>
    devices.value.filter(device => device.status === 'OFFLINE')
  )

  const temperatureData = computed(() =>
    deviceData.value.filter(data => data.metricName === 'temperature')
  )

  const humidityData = computed(() =>
    deviceData.value.filter(data => data.metricName === 'humidity')
  )

  // 获取设备列表
  const fetchDevices = async () => {
    try {
      loading.value = true
      devices.value = await deviceApi.getDevices()
    } catch (error) {
      console.error('获取设备列表失败:', error)
    } finally {
      loading.value = false
    }
  }

  // 获取最新数据
  const fetchLatestData = async () => {
    try {
      deviceData.value = await deviceApi.getLatestData()
    } catch (error) {
      console.error('获取最新数据失败:', error)
    }
  }

  // 获取统计信息
  const fetchStatistics = async () => {
    try {
      statistics.value = await deviceApi.getStatistics()
    } catch (error) {
      console.error('获取统计信息失败:', error)
    }
  }

  // 添加告警
  const addAlert = (alert: AlertInfo) => {
    alerts.value.unshift(alert)
    // 限制告警数量，保留最新的50条
    if (alerts.value.length > 50) {
      alerts.value = alerts.value.slice(0, 50)
    }
  }

  // 清除告警
  const clearAlerts = () => {
    alerts.value = []
  }

  // 更新设备数据
  const updateDeviceData = (newData: DeviceData) => {
    // 更新设备数据列表
    const existingIndex = deviceData.value.findIndex(
      data => data.deviceId === newData.deviceId && data.metricName === newData.metricName
    )

    if (existingIndex >= 0) {
      deviceData.value[existingIndex] = newData
    } else {
      deviceData.value.push(newData)
    }

    // 检查是否需要添加告警
    if (newData.status !== 'normal') {
      const device = devices.value.find(d => d.id === newData.deviceId)
      if (device) {
        const alertLevel = newData.status === 'critical' ? 'critical' :
                          newData.status === 'high' ? 'error' : 'warning'

        const alert: AlertInfo = {
          id: `${newData.deviceId}-${newData.metricName}-${Date.now()}`,
          deviceId: newData.deviceId,
          deviceName: device.deviceName,
          metricName: newData.metricName,
          value: newData.metricValue,
          status: newData.status,
          timestamp: newData.recordTime,
          level: alertLevel
        }

        addAlert(alert)
      }
    }
  }

  // 设置WebSocket连接状态
  const setWsConnected = (connected: boolean) => {
    wsConnected.value = connected
  }

  // 初始化数据
  const initData = async () => {
    await Promise.all([
      fetchDevices(),
      fetchLatestData(),
      fetchStatistics()
    ])
  }

  return {
    // 状态
    devices,
    deviceData,
    statistics,
    alerts,
    loading,
    wsConnected,

    // 计算属性
    onlineDevices,
    offlineDevices,
    warningDevices,
    temperatureData,
    humidityData,

    // 方法
    fetchDevices,
    fetchLatestData,
    fetchStatistics,
    addAlert,
    clearAlerts,
    updateDeviceData,
    setWsConnected,
    initData
  }
})