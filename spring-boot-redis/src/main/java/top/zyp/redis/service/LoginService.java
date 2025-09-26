package top.zyp.redis.service;

import top.zyp.redis.dto.LoginRequest;
import top.zyp.redis.vo.LoginResponse;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/25
 * @Version: 1.0
 */


public interface LoginService {
    /**
     * 验证码登录
     *
     * @param loginRequest 登录请求参数
     * @return 登录响应
     */
    LoginResponse login(LoginRequest loginRequest);
}
