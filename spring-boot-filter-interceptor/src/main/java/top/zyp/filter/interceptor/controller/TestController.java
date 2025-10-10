package top.zyp.filter.interceptor.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.zyp.filter.interceptor.result.Result;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/26
 * @Version: 1.0
 */

@RestController
@Slf4j
@RequestMapping("/api")
public class TestController {
    @GetMapping("/test")
    public Result<String> test() {
        log.info("进入 TstController");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e){
            throw new RuntimeException(e);
        }
        return Result.ok("hello world");
    }
    @GetMapping("/pay/{id}")
    public Result<String> pay(@PathVariable int id) {
        log.info("开始支付");
        return Result.ok("支付成功,订单号："+id);
    }

}
