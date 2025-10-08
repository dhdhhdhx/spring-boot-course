import axios, {type AxiosInstance, type AxiosResponse, type InternalAxiosRequestConfig} from 'axios'

// 创建 axios 实例
const request: AxiosInstance = axios.create({
    baseURL: 'http://localhost:8080/api',
    timeout: 5000,
    headers: {
        'Content-Type': 'application/json'
    }
})

// 请求拦截器
request.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        // 从 localStorage 获取 token
        const token = localStorage.getItem('token')
        if (token && config.headers) {
            config.headers['token'] = token
        }
        return config
    },
    (error) => {
        console.error('请求错误：', error)
        return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    (response: AxiosResponse) => {
        const res = response.data
        // 根据业务状态码处理
        if (res.code === 0) {
            return response
        } else {
            console.error('业务错误：', res.msg)
            return Promise.reject(new Error(res.msg || '请求失败'))
        }
    },
    (error) => {
        console.error('响应错误：', error.msg)

        // 处理 HTTP 错误状态码
        if (error.response) {
            switch (error.response.status) {
                case 401:
                    console.error('未授权，请重新登录')
                    localStorage.removeItem('token')
                    break
                case 403:
                    console.error('拒绝访问')
                    break
                case 404:
                    console.error('请求的资源不存在')
                    break
                case 500:
                    console.error('服务器错误')
                    break
                default:
                    console.error(`连接错误 ${error.response.status}`)
            }
        }

        return Promise.reject(error)
    }
)

export default request