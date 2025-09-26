package top.zyp.redis.cache;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/25
 * @Version: 1.0
 */


public class RedisKeys {
    public static String getSmsKey(String phone) {
        return "sms:captcha" + phone;
    }
}
