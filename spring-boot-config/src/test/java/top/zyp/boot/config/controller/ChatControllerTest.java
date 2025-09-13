package top.zyp.boot.config.controller;

import cn.hutool.ai.core.AIService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
class AIServiceTest {

    @Resource
    private AIService aiService;   // 就是刚才 AIConfig 里注入的实现类

    @Test
    @DisplayName("单轮对话 - 返回内容非空")
    void testChat() throws Exception {
        String question = "用一句话介绍 Spring Boot";
        String answer = aiService.chat(question);

        log.info("问题：{}", question);
        log.info("回答：{}", answer);

        assertNotNull(answer, "回答不应为 null");
        assertTrue(answer.length() > 0, "回答长度应 > 0");
    }

    /* 可选：流式测试 - 观察逐字返回 */
    @Test
    @DisplayName("流式对话 - 逐字打印")
    void testStreamChat() throws Exception {
        String question = "写一首关于春天的五言绝句";
        log.info("=== 流式开始 ===");
        aiService.chat(question, chunk -> log.info("chunk: {}", chunk));
        log.info("=== 流式结束 ===");
    }
}