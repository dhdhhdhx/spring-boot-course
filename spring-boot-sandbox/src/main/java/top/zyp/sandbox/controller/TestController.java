package top.zyp.sandbox.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/23
 * @Version: 1.0
 */

@RestController
public class TestController {
    @GetMapping()
    public String test() {
        return "hello world";
    }
}
