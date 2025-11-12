import { ref, onUnmounted } from 'vue'
import { useDeviceStore } from '@/stores/device'
import type { WebSocketMessage, DeviceData } from '@/types'

export function useWebSocket(userId = 'dashboard') {
  const ws = ref<WebSocket | null>(null)
  const reconnectTimer = ref<NodeJS.Timeout | null>(null)
  const heartbeatTimer = ref<NodeJS.Timeout | null>(null)
  const deviceStore = useDeviceStore()

  // WebSocket连接地址
  const wsUrl = `ws://localhost:8080/ws/device?userId=${userId}`

  // 连接WebSocket
  const connect = () => {
    try {
      console.log('正在连接WebSocket...')
      ws.value = new WebSocket(wsUrl)

      ws.value.onopen = () => {
        console.log('WebSocket连接成功')
        deviceStore.setWsConnected(true)

        // 清除重连定时器
        if (reconnectTimer.value) {
          clearInterval(reconnectTimer.value)
          reconnectTimer.value = null
        }

        // 启动心跳
        startHeartbeat()

        // 请求最新数据
        sendMessage({
          type: 'GET_LATEST_DATA',
          timestamp: Date.now(),
          payload: null
        })
      }

      ws.value.onmessage = (event) => {
        try {
          const message: WebSocketMessage = JSON.parse(event.data)
          handleMessage(message)
        } catch (error) {
          console.error('解析WebSocket消息失败:', error)
        }
      }

      ws.value.onclose = (event) => {
        console.log('WebSocket连接关闭:', event.code, event.reason)
        deviceStore.setWsConnected(false)

        // 清除心跳
        stopHeartbeat()

        // 自动重连
        if (!event.wasClean) {
          startReconnect()
        }
      }

      ws.value.onerror = (error) => {
        console.error('WebSocket错误:', error)
        deviceStore.setWsConnected(false)
      }
    } catch (error) {
      console.error('WebSocket连接失败:', error)
      startReconnect()
    }
  }

  // 断开连接
  const disconnect = () => {
    stopHeartbeat()
    stopReconnect()

    if (ws.value) {
      ws.value.close()
      ws.value = null
    }

    deviceStore.setWsConnected(false)
  }

  // 发送消息
  const sendMessage = (message: any) => {
    if (ws.value && ws.value.readyState === WebSocket.OPEN) {
      ws.value.send(JSON.stringify(message))
    } else {
      console.warn('WebSocket未连接，无法发送消息')
    }
  }

  // 处理收到的消息
  const handleMessage = (message: WebSocketMessage) => {
    const { type, payload } = message

    switch (type) {
      case 'CONNECTION':
        console.log('收到连接消息:', payload)
        break

      case 'LATEST_DATA':
        console.log('收到最新数据:', payload)
        if (Array.isArray(payload)) {
          payload.forEach((data: DeviceData) => {
            deviceStore.updateDeviceData(data)
          })
        }
        break

      case 'DEVICE_DATA':
        console.log('收到设备数据更新:', payload)
        deviceStore.updateDeviceData(payload)
        break

      case 'HEARTBEAT':
        console.log('收到心跳响应:', payload)
        break

      case 'ERROR':
        console.error('收到错误消息:', payload)
        break

      default:
        console.log('未知消息类型:', type, payload)
    }
  }

  // 启动心跳
  const startHeartbeat = () => {
    heartbeatTimer.value = setInterval(() => {
      sendMessage({
        type: 'HEARTBEAT',
        timestamp: Date.now(),
        payload: 'ping'
      })
    }, 30000) // 30秒心跳
  }

  // 停止心跳
  const stopHeartbeat = () => {
    if (heartbeatTimer.value) {
      clearInterval(heartbeatTimer.value)
      heartbeatTimer.value = null
    }
  }

  // 启动重连
  const startReconnect = () => {
    if (reconnectTimer.value) return

    console.log('5秒后尝试重连...')
    reconnectTimer.value = setInterval(() => {
      console.log('尝试重新连接WebSocket...')
      connect()
    }, 5000)
  }

  // 停止重连
  const stopReconnect = () => {
    if (reconnectTimer.value) {
      clearInterval(reconnectTimer.value)
      reconnectTimer.value = null
    }
  }

  // 组件卸载时清理
  onUnmounted(() => {
    disconnect()
  })

  return {
    connect,
    disconnect,
    sendMessage
  }
}