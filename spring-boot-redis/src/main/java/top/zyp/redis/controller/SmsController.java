package top.zyp.redis.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.zyp.redis.resylt.Result;
import top.zyp.redis.service.SmsService;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/25
 * @Version: 1.0
 */


@RestController
@RequestMapping("/api")
public class SmsController {
    @Resource
    private SmsService smsService;
    @GetMapping("/sms/send")
    public Result<Boolean> sendSms(@RequestParam String phone) {
        return Result.ok(smsService.sendSms(phone));
    }
}
