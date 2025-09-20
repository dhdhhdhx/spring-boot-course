package top.zyp.boot.exception.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import top.zyp.boot.exception.common.Result;
import top.zyp.boot.exception.entity.User;
import top.zyp.boot.exception.service.TestService;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/19
 * @Version: 1.0
 */

@RestController
@RequestMapping("/test")
public class TestController {
    @Resource
    private TestService testService;

    @RequestMapping("/{id}")
    public Result<String> getInfo(@PathVariable int id) {
        if (id == 1) {
            testService.method1();
        } else if (id == 2) {
            testService.method2();
        } else {
            int n = 1 / 0;
            return Result.ok("请求成功");
        }
        return Result.ok("请求成功");
    }
    @PostMapping("/user")
    public Result<User> createUser(@Valid @RequestBody User user) {
        return Result.ok(user);
    }
}
