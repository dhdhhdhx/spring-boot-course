package top.zyp.redis.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.zyp.redis.dto.UserRegisterDTO;
import top.zyp.redis.entity.User;
import top.zyp.redis.resylt.Result;
import top.zyp.redis.service.UserService;
import top.zyp.redis.vo.UserRegisterVO;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/26
 * @Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 创建用户（入参密码为明文，后端 BCrypt 加密）
     */
    @PostMapping("/users")
    public Result<UserRegisterVO> createUser(@Valid @RequestBody UserRegisterDTO urdto) {
        User entity = new User();
        BeanUtils.copyProperties(urdto, entity);
        boolean saved = userService.createUser(entity);
        UserRegisterVO vo = new UserRegisterVO();
        BeanUtils.copyProperties(entity, vo);
        return saved ? Result.ok(vo) : Result.error("用户创建失败");
    }


}
