import axios from 'axios'
import type { Device, DeviceData, DeviceStatistics } from '@/types'

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    console.error('API请求错误:', error)
    return Promise.reject(error)
  }
)

// 设备相关API
export const deviceApi = {
  // 获取所有设备
  getDevices(): Promise<Device[]> {
    return api.get('/devices').then(res => res.data)
  },

  // 获取最新数据
  getLatestData(): Promise<DeviceData[]> {
    return api.get('/devices/latest-data').then(res => res.data)
  },

  // 获取设备统计信息
  getStatistics(): Promise<DeviceStatistics> {
    return api.get('/devices/statistics').then(res => res.data)
  },

  // 获取设备历史数据
  getDeviceHistory(deviceId: number, hours = 24): Promise<DeviceData[]> {
    return api.get(`/devices/${deviceId}/history`, {
      params: { hours }
    }).then(res => res.data)
  },

  // 获取指定设备的最新数据
  getDeviceLatestData(deviceId: number): Promise<DeviceData[]> {
    return api.get(`/devices/${deviceId}/latest-data`).then(res => res.data)
  },

  // 模拟数据上报
  reportData(deviceId: number, metricName: string, metricValue: number, unit?: string, status = 'normal') {
    return api.post(`/devices/${deviceId}/report`, null, {
      params: { metricName, metricValue, unit, status }
    })
  }
}

export default api