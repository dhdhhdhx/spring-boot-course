package top.mqxu.week1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/4
 * @Version: 1.0
 */

@RestController
public class HelloController {
    @GetMapping("/hello")
    public String getHello(){return "Hello Wordld111!";}

    @GetMapping("/list")
    public List<String> getList(){
        return List.of("aaa","bbb","ccc");
    }
}
