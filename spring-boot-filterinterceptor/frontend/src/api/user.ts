import request from '@/utils/request'
import type {LoginRequest, LoginResponse, Result, User} from './types'

/**
 * 用户登录
 */
export const login = (data: LoginRequest) => {
    return request.post<Result<LoginResponse>>('/login', data)
}

/**
 * 获取个人信息（需要 ADMIN 或 USER 权限）
 */
export const getUserProfile = () => {
    return request.post<Result<User>>('/profile')
}

/**
 * 获取部门信息（仅 ADMIN 权限可访问）
 */
export const getDepartmentInfo = () => {
    return request.post<Result<string>>('/department')
}