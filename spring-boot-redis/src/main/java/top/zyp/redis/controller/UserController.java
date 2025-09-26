package top.zyp.redis.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.zyp.redis.entity.User;
import top.zyp.redis.resylt.Result;
import top.zyp.redis.service.UserService;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/26
 * @Version: 1.0
 */

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 创建用户（入参密码为明文，后端 BCrypt 加密）
     */
    @PostMapping
    public Result<User> createUser(@Valid @RequestBody User user) {
        boolean saved = userService.createUser(user);
        return saved ? Result.ok(user)
                : Result.error("用户创建失败");
    }
}
