// 通用响应接口
export interface Result<T = any> {
  code: number
  message: string
  data: T
}

// 登录请求
export interface LoginRequest {
  username: string
  password: string
}

// 登录响应
export interface LoginResponse {
  userId: number
  username: string
  role: string
  token: string
}

// 用户信息
export interface User {
  id: number
  username: string
  role: string
  password?: string
}