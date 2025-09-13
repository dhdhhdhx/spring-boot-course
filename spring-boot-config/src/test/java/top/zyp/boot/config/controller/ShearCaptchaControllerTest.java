package top.zyp.boot.config.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 验证码接口测试
 */
@SpringBootTest
@AutoConfigureMockMvc
class CaptchaControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper; // 用于序列化 Map

    @Test
    @DisplayName("1. /captcha 能正常返回图片并把验证码存进 Session")
    void captchaGenerate() throws Exception {
        MockHttpSession session = new MockHttpSession();

        // 1. 请求验证码
        mvc.perform(get("/captcha").session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"))
                .andExpect(header().string("Pragma", "no-cache"));

        // 2. 取出 Session 里保存的验证码值
        String codeInSession = (String) session.getAttribute("captcha");
        System.out.println("本次验证码 = " + codeInSession);

        // 3. 简单断言：验证码存在且 4 位
        assert codeInSession != null && codeInSession.length() == 4;
    }

    @Test
    @DisplayName("2. /verify 正确码返回 success=true")
    void verifyCorrect() throws Exception {
        MockHttpSession session = new MockHttpSession();

        /* ① 先调一次 /captcha 把验证码存 Session */
        mvc.perform(get("/captcha").session(session));

        String correctCode = (String) session.getAttribute("captcha");

        /* ② 用正确码调 /verify */
        mvc.perform(post("/verify")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("code", correctCode))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("3. /verify 错误码返回 success=false")
    void verifyWrong() throws Exception {
        MockHttpSession session = new MockHttpSession();

        /* ① 先调 /captcha */
        mvc.perform(get("/captcha").session(session));

        /* ② 用错误码调 /verify */
        mvc.perform(post("/verify")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("code", "0000"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    /* --------- 彩蛋：如果你只想快速造一个验证码而不走 MockMvc，可以这么玩 --------- */
    @Test
    @DisplayName("工具方法：直接生成一张验证码图片并打印字符")
    void quickCaptcha() {
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(150, 50, 4, 4);
        System.out.println("快速验证码 = " + captcha.getCode());
        // captcha.write(FileUtil.file("d:/test.jpg")); // 如需落盘
    }
}