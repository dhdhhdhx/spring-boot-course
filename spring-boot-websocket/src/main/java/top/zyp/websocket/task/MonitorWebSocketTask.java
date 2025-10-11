package top.zyp.websocket.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.zyp.websocket.handler.MonitorWebSocketHandler;
import top.zyp.websocket.service.ServerMonitorService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class MonitorWebSocketTask {

    private final ServerMonitorService monitorService;
    private final MonitorWebSocketHandler monitorHandler;

    /**
     * 每 5 分钟采集 + 推送
            */
//    @Scheduled(cron = "0 */5 * * * ?")
    @Scheduled(cron = "0 */1 * * * ?")

    public void pushMonitorReport() {
        try {
            double cpu    = monitorService.getCpuUsage();
            double memory = monitorService.getMemoryUsage();

            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd HH:mm:ss"));

            // 1. 正常健康报告
            String report = String.format(
                    "【服务器健康】%s  CPU：%.2f%%  内存：%.2f%%",
                    time, cpu, memory
            );
            monitorHandler.broadcast(report);

            // 2. 阈值告警（超过即推送）
            double cpuThreshold    = monitorService.getCpuThreshold();
            double memoryThreshold = monitorService.getMemoryThreshold();

            if (cpu > cpuThreshold) {
                monitorHandler.broadcast(String.format(
                        "⚠️【CPU告警】%.2f%% > %.2f%%", cpu, cpuThreshold
                ));
            }
            if (memory > memoryThreshold) {
                monitorHandler.broadcast(String.format(
                        "⚠️【内存告警】%.2f%% > %.2f%%", memory, memoryThreshold
                ));
            }

            log.info("[WS-监控] 推送完成，在线客户端：{}", MonitorWebSocketHandler.SESSIONS.size());

        } catch (Exception e) {
            log.error("[WS-监控] 推送异常", e);
        }
    }
}