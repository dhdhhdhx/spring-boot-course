package top.zyp.boot.config.controller;

import cn.hutool.ai.core.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/13
 * @Version: 1.0
 */


@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
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
    @GetMapping("/stream")
    public SseEmitter stream(@RequestParam String q) {


        SseEmitter emitter = new SseEmitter();

        // 仅 Consumer 内部可能抛 IOException，转成运行时异常即可
        Consumer<String> sender = chunk -> {
            try {
                emitter.send(chunk);
            } catch (IOException e) {
                throw new RuntimeException("SSE send error", e);
            }
        };

        // 外层无受检异常，无需 try-catch
        aiService.chat(q, sender);
        return emitter;
    }
}
