# 设备监控大屏前端

基于 Vue 3 + TypeScript + Vite + ECharts 构建的设备监控实时数据大屏。

## 功能特性

- 🚀 Vue 3 + TypeScript + Composition API
- 📊 实时数据可视化（折线图、饼图）
- 🔌 WebSocket 实时数据推送
- 📱 响应式设计，支持大屏和移动端
- ⚡ 状态管理（Pinia）
- 🎨 现代化 UI 设计（Element Plus）
- 📈 设备数据趋势监控
- 🚨 实时告警面板

## 技术栈

- **框架**: Vue 3
- **语言**: TypeScript
- **构建工具**: Vite
- **图表库**: ECharts + vue-echarts
- **UI 组件**: Element Plus
- **状态管理**: Pinia
- **HTTP 请求**: Axios
- **实时通信**: WebSocket
- **工具库**: @vueuse/core, dayjs

## 项目结构

```
device-monitor/
├── src/
│   ├── api/                 # API 接口
│   ├── components/          # 组件
│   │   ├── charts/         # 图表组件
│   │   ├── AlertPanel.vue  # 告警面板
│   │   └── StatusCard.vue  # 状态卡片
│   ├── composables/        # 组合式函数
│   │   └── useWebSocket.ts # WebSocket 连接
│   ├── stores/            # 状态管理
│   │   └── device.ts      # 设备数据状态
│   ├── types/             # 类型定义
│   ├── App.vue            # 根组件
│   ├── main.ts            # 入口文件
│   └── style.css          # 全局样式
├── package.json
├── vite.config.ts         # Vite 配置
├── tsconfig.json          # TypeScript 配置
└── README.md
```

## 快速开始

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

### 构建生产版本

```bash
npm run build
```

### 预览生产版本

```bash
npm run preview
```

## 主要功能

### 1. 实时数据监控
- 温度、湿度趋势图
- 设备状态分布饼图
- 自动数据刷新（5秒间隔）

### 2. WebSocket 实时通信
- 自动重连机制
- 心跳保活
- 实时数据推送

### 3. 告警系统
- 实时告警展示
- 告警级别分类（警告、错误、严重）
- 告警详情查看
- 告警清除功能

### 4. 状态卡片
- 设备总数统计
- 在线设备数量
- 告警设备数量
- 今日告警次数

### 5. 响应式设计
- 支持大屏显示
- 移动端适配
- 自适应布局

## API 接口

后端接口需要与 Spring Boot WebSocket 模块配合：

- `GET /api/devices` - 获取设备列表
- `GET /api/devices/latest-data` - 获取最新数据
- `GET /api/devices/statistics` - 获取统计信息
- `GET /api/devices/{id}/history` - 获取历史数据
- `WebSocket /ws/device` - 实时数据推送

## 配置说明

### Vite 配置

```typescript
// vite.config.ts
export default defineConfig({
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/ws': {
        target: 'ws://localhost:8080',
        ws: true
      }
    }
  }
})
```

### 环境变量

开发环境下，前端运行在 `http://localhost:3000`，后端 API 运行在 `http://localhost:8080`。

## 开发说明

### 添加新的图表类型

1. 在 `src/components/charts/` 目录下创建新的图表组件
2. 使用 ECharts 配置图表选项
3. 在 `App.vue` 中引入并使用

### 扩展告警类型

1. 在 `src/types/index.ts` 中定义新的告警类型
2. 在 `src/components/AlertPanel.vue` 中添加对应的样式和逻辑
3. 更新 `src/stores/device.ts` 中的告警处理逻辑

### 自定义主题

修改 `src/style.css` 中的 CSS 变量来自定义主题色彩。

## 部署

### Docker 部署

```dockerfile
FROM node:18-alpine

WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production

COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=0 /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### 静态文件部署

构建完成后，将 `dist` 目录部署到任何静态文件服务器即可。

## 浏览器支持

- Chrome >= 87
- Firefox >= 78
- Safari >= 14
- Edge >= 88
