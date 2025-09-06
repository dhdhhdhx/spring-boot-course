package cn.edu.njuit.yapeng.cn_edu_njuit_cloud.controller;

import cn.edu.njuit.yapeng.cn_edu_njuit_cloud.service.SmsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/5
 * @Version: 1.0
 */

@RestController
public class SmsController {
    @Resource
    private SmsService smsService;

    @GetMapping("/sms")
    public void sendSms(String phone){
        smsService.sendSms(phone);
    }

}
