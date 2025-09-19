package top.zyp.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.zyp.mp.entity.UserAccount;

import java.util.List;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/18
 * @Version: 1.0
 */


public interface UserAccountService extends IService<UserAccount> {

    /** 创建单个用户（自动加密密码） */
    boolean createUser(UserAccount user);

    /** 批量创建用户（自动加密密码） */
    boolean createUsers(List<UserAccount> users);
}
