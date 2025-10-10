package top.zyo.filterinterceptor.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.zyo.filterinterceptor.result.Result;

/**
 * @Author: calm_sunset
 * @Date: 2025/10/7
 * @Version: 1.0
 */

@Slf4j
@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/test/filter")
    public Result<String> testFilter(@RequestParam String name) {
        return Result.ok("Hello, " + name);
    }
    @GetMapping("/pay/{id}")
    public Result<String> pay(@PathVariable long id) {
        log.info("开始支付");
        return Result.ok("支付成功，订单号：" + id);
    }
}
