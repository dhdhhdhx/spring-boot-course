package top.zyp.redis.service;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/25
 * @Version: 1.0
 */


public interface SmsService {
    /**
     * 发送短信
     *
     * @param phone ⼿机号
     */
    boolean sendSms(String phone);
}
