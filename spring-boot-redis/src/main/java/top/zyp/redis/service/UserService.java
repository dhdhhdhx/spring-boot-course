package top.zyp.redis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.zyp.redis.entity.User;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/26
 * @Version: 1.0
 */


public interface UserService extends IService<User> {
    /** 创建单个用户（自动加密密码） */
    boolean createUser(User user);
    boolean existsPhone(String phone);
}
