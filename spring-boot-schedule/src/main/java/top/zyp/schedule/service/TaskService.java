package top.zyp.schedule.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @Author: calm_sunset
 * @Date: 2025/10/10
 * @Version: 1.0
 */


@Slf4j
@Service
public class TaskService {

    /**
     * 1. 固定延迟执行：上一次任务结束后，延迟 fixedDelay 毫秒再执行下一次
     * 场景：任务执行时间不固定，需等上一次完成（如数据同步，避免重复处理）
     */
    @Scheduled(fixedDelay = 5000)   // 延迟 5 秒后开始下一次
    public void fixedDelayTask() {
        log.info("【固定延迟任务】执行时间：{}，执行线程：{}", LocalDateTime.now(), Thread.currentThread().getName());

        // 模拟任务耗时 1 秒
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 2. 固定频率执行：上一次任务开始后，间隔 fixedRate 毫秒执行下一次
     * 场景：任务执行时间短，需按固定频率执行（如实时监控）
     */
    @Scheduled(fixedRate = 5000)    // 每 5 秒执行一次
    public void fixedRateTask() {
        log.info("【固定频率任务】执行时间：{}，执行线程：{}", LocalDateTime.now(), Thread.currentThread().getName());
    }

    /**
     * 3. 初始延迟执行：项目启动后，延迟 initialDelay 毫秒，再按 fixedRate 执行
     * 场景：任务依赖其他组件初始化（如数据库连接池启动）
     */
    @Scheduled(initialDelay = 10000, fixedRate = 5000) // 启动 10 秒后开始，每 5 秒一次
    public void initialDelayTask() {
        log.info("【初始延迟任务】执行时间：{}，执行线程：{}", LocalDateTime.now(), Thread.currentThread().getName());
    }

    /**
     * 4. Cron 表达式执行：支持复杂时间规则（最灵活）
     * 场景：固定时间执行，如每天 0 点、每周一 14 点等
     * 下面示例：每分钟执行一次
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void cronTask() {
        log.info("【Cron 任务】执行时间：{}，执行线程：{}", LocalDateTime.now(), Thread.currentThread().getName());
    }
}
