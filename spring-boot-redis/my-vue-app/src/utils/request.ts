/**
 * HTTP请求工具模块
 * 基于axios封装，提供统一的请求/响应处理、错误处理、认证等功能
 */
import axios, {
  type AxiosInstance,
  type InternalAxiosRequestConfig,
  type AxiosResponse,
} from "axios";
import { ElMessage } from "element-plus";
import type { ApiResponse } from "../types/api";

/**
 * 创建axios实例
 * 统一配置请求的基础设置，包括baseURL、超时时间、请求头等
 */
const request: AxiosInstance = axios.create({
  baseURL: "http://localhost:8888/api", // API服务器地址
  timeout: 10000, // 请求超时时间10秒
  headers: {
    "Content-Type": "application/json;charset=UTF-8", // 默认请求头，指定内容类型为JSON
  },
});

/**
 * 请求拦截器
 * 在每次请求发送前执行，主要用于：
 * 1. 添加认证token到请求头
 * 2. 统一设置请求配置
 * 3. 记录请求错误
 */
request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 从localStorage获取token
    const token = localStorage.getItem("token");

    // 如果存在token，则添加到请求头的Authorization字段
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => {
    // 请求发送前出错时的处理
    console.error("请求错误：", error);
    return Promise.reject(error);
  }
);

/**
 * 响应拦截器
 * 在收到响应后执行，主要用于：
 * 1. 统一处理响应数据
 * 2. 根据业务状态码处理不同情况
 * 3. 统一错误处理和用户提示
 */
request.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const { data } = response;

    // 如果HTTP状态码为200，说明网络请求成功
    if (response.status === 200) {
      return response;
    }

    // 其他状态码视为失败，显示错误信息并抛出异常
    ElMessage.error(data.msg || "请求失败");
    return Promise.reject(new Error(data.msg || "请求失败"));
  },
  (error) => {
    // 处理HTTP错误状态码和网络错误
    let message = "请求失败";

    if (error.response) {
      // 服务器返回了错误状态码
      switch (error.response.status) {
        case 401:
          message = "未授权，请重新登录";
          // 401表示token无效或过期，清除本地存储的认证信息并跳转到登录页
          localStorage.removeItem("token");
          localStorage.removeItem("userPhone");
          localStorage.removeItem("loginTime");
          window.location.href = "/login";
          break;
        case 403:
          message = "拒绝访问"; // 权限不足
          break;
        case 404:
          message = "请求资源不存在"; // 接口不存在
          break;
        case 500:
          message = "服务器内部错误"; // 服务器错误
          break;
        case 502:
          message = "网关错误";
          break;
        case 503:
          message = "服务不可用"; // 服务暂时不可用
          break;
        case 504:
          message = "网关超时"; // 网关超时
          break;
        default:
          message = `请求失败：${error.response.status}`;
      }
    } else if (error.request) {
      // 请求已发送但没有收到响应（网络错误）
      message = "网络连接失败";
    }

    // 如果既没有response也没有request，则是请求配置出错，使用默认错误信息

    console.error("响应错误：", error);
    ElMessage.error(message); // 向用户显示友好的错误提示
    return Promise.reject(error);
  }
);

// 导出配置好的axios实例，供其他模块使用
export default request;