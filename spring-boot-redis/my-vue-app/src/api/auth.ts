import request from "../utils/request";
import type { LoginRequest, ApiResponse,RegisterRequest } from "../types/api";
// 相关API - 只包含后端实际提供的接⼝
export const authApi = {
    // 发送短信验证码
    sendSms: (phone: string): Promise<ApiResponse> => {
        return request.get(`/sms/send?phone=${phone}`).then((res) => res.data);
    },
    // ⽤户登录（短信验证码登录）
    login: (data: LoginRequest): Promise<ApiResponse> => {
        return request.post("/login", data).then((res) => res.data);
    },
      // 用户注册
  register: (data: RegisterRequest): Promise<ApiResponse> => {
    return request.post("/users", data).then(res => res.data);
  }
};