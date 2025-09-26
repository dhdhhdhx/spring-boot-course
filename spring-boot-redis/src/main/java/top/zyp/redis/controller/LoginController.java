package top.zyp.redis.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.zyp.redis.dto.LoginRequest;
import top.zyp.redis.resylt.Result;
import top.zyp.redis.service.LoginService;
import top.zyp.redis.vo.LoginResponse;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/25
 * @Version: 1.0
 */


@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class LoginController {
    private final LoginService loginService;
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = loginService.login(loginRequest);
        return Result.ok(loginResponse);
    }
}
