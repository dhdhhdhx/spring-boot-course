// 设备相关类型定义
export interface Device {
  id: number
  deviceName: string
  deviceCode: string
  location?: string
  description?: string
  longitude?: number
  latitude?: number
  status: 'OFFLINE' | 'ONLINE' | 'WARNING' | 'ERROR' | 'MAINTENANCE'
  lastWarningTime?: string
  deleted?: number
  createTime: string
  updateTime: string
}

// 设备数据类型
export interface DeviceData {
  id: number
  deviceId: number
  metricName: string
  metricValue: number
  unit?: string
  status: string
  recordTime: string
}

// 统计信息类型
export interface DeviceStatistics {
  totalDevices: number
  onlineDevices: number
  offlineDevices: number
  warningDevices: number
  statusCount: Record<string, number>
}

// WebSocket消息类型
export interface WebSocketMessage {
  type: string
  timestamp: number
  payload: any
}

// 告警信息类型
export interface AlertInfo {
  id: string
  deviceId: number
  deviceName: string
  metricName: string
  value: number
  status: string
  timestamp: string
  level: 'warning' | 'error' | 'critical'
}

// 图表数据类型
export interface ChartDataPoint {
  timestamp: string
  value: number
  deviceName?: string
}

export interface PieChartData {
  name: string
  value: number
  itemStyle?: {
    color: string
  }
}