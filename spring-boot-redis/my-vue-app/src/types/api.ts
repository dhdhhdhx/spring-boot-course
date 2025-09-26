// 登录请求参数
export interface LoginRequest {
 phone: string;
 code: string;
 }
 // 统⼀响应数据类型
export interface ApiResponse {
 code: number;
 msg: string;
 data: any;
 }