// 登录请求参数
export interface LoginRequest {
 phone: string;
 code: string;
 }
 // 注册请求参数
 // 统⼀响应数据类型
 export interface RegisterRequest {
 account: String;
 password: String;
 nickname?: String;
 phone: string;
 avatar?: String;
 }
export interface ApiResponse {
 code: number;
 msg: string;
 data: any;
 }