package top.zyp.redis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.zyp.redis.entity.User;
import top.zyp.redis.exception.ServerException;
import top.zyp.redis.mapper.UserMapper;
import top.zyp.redis.service.UserService;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/26
 * @Version: 1.0
 */
@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();


    /**
     * 单个用户创建
     */
    @Override
    public boolean createUser(User user) {
        if (existsPhone(user.getPhone())) {
            throw new ServerException("该手机号已经被注册了");
        }
        processUserBeforeSave(user);
        return this.save(user);
    }

    @Override
    public boolean existsPhone(String phone){
        return this.count(
                new LambdaQueryWrapper<User>().eq(User::getPhone, phone)) > 0;
    }

    /**
     * 公共处理逻辑：密码加密 & 默认值
     */
    private void processUserBeforeSave(User user) {
        // 密码加密：如果不是BCrypt格式（$2开头），就加密
        if (user.getPassword() != null && !user.getPassword().startsWith("$2")) {
            user.setPassword(ENCODER.encode(user.getPassword()));
        }
        // 默认未删除
        if (user.getDeleted() == null) {
            user.setDeleted(0);
        }
        // 默认版本号
        if (user.getVersion() == null) {
            user.setVersion(0);
        }
    }


}
