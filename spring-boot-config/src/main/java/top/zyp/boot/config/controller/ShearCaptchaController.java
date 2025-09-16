package top.zyp.boot.config.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.ShearCaptcha;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/13
 * @Version: 1.0
 */

@RestController
public class ShearCaptchaController {
    @Value("${captcha.expose-header:false}")
    private boolean exposeHeader;

    @GetMapping("/ShearCaptcha")
    public void generateCaptcha(HttpServletResponse response, HttpSession session) throws IOException {


        // 创建验证码对象：宽、高、验证码位数、干扰线数
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(150, 50, 4, 4);
        // 获取验证码文本
        String code = captcha.getCode();

        // 将验证码存入 Session（或 Redis）
        session.setAttribute("captcha", code);

        // 设置响应头
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        // 3. 【测试环境专用】把明文再丢一个响应头，方便 Apifox 自动提取
        if (exposeHeader) {
            response.setHeader("X-Captcha-Code", code);
        }

        // 输出图片
        captcha.write(response.getOutputStream());
    }

    /**
     *ShearCaptcha
     */
    @GetMapping("/CircleCaptcha")
    public void generateLineCaptcha(HttpServletResponse response, HttpSession session) throws IOException {
        // 1. 创建圆圈干扰验证码
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(200, 100, 4, 20);
        String code = captcha.getCode();

        // 2. 存 Session（或 Redis）
        session.setAttribute("captcha", code);

        // 设置响应头
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        // 3. 【测试环境专用】把明文再丢一个响应头，方便 Apifox 自动提取
        if (exposeHeader) {
            response.setHeader("X-Captcha-Code", code);
        }

        // 输出图片
        captcha.write(response.getOutputStream());
    }



    @PostMapping("/verify")
    public Map<String, Object> verifyCaptcha(@RequestBody Map<String, String> body, HttpSession session) {
        String inputCode = body.get("code");
        String sessionCode = (String) session.getAttribute("captcha");

        boolean success = sessionCode != null && sessionCode.equalsIgnoreCase(inputCode);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return result;
    }
}
