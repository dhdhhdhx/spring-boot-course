package top.zyp.boot.config.controller;

import cn.hutool.ai.core.AIService;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.function.Consumer;

import java.io.IOException;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/13
 * @Version: 1.0
 */


@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final AIService aiService;

    /**
     * 普通对话
     */
    @GetMapping("/chat")
    public String chat(@RequestParam String q) {
        return aiService.chat(q);
    }

    /**
     * 流式对话（SSE）
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@RequestParam String q,
                             HttpServletResponse response) {
        // 0. 延长超时（0 = 永不超时）
        SseEmitter emitter = new SseEmitter(0L);
        response.setCharacterEncoding("UTF-8");

        emitter.onCompletion(() -> log.info("SSE 完成"));
        emitter.onTimeout(() -> log.warn("SSE 超时"));

        // 1. 把原始 chunk 转成纯文本再推送
        Consumer<String> sender = rawLine -> {
            if (!rawLine.startsWith("data:")) return;
            String jsonStr = rawLine.substring(5).trim();
            if ("[DONE]".equals(jsonStr)) {
                try {
                    emitter.send("[DONE]");
                    emitter.complete();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
            // 提取 content
            String content = JSONUtil.parseObj(jsonStr)
                    .getByPath("choices[0].delta.content", String.class);
            if (content != null) {
                try {
                    emitter.send(content);
                } catch (IOException e) {
                    throw new RuntimeException("SSE send error", e);
                }
            }
        };

        // 2. 异步调用，不阻塞 Tomcat IO 线程
        new Thread(() -> {
            try {
                aiService.chat(q, sender);
            } catch (Exception e) {
                log.error("AI 调用异常", e);
                try {
                    emitter.send("服务异常");
                    emitter.completeWithError(e);
                } catch (IOException ignored) {}
            }
        }, "deepseek-stream").start();

        return emitter;

    }

}
