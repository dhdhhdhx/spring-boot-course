package top.zyp.websocket.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 服务器监控任务
 */
@Slf4j
@Service
public class ServerMonitorService {

    private final DecimalFormat df = new DecimalFormat("#.00"); // 保留两位小数

    @Getter
    @Value("${server.monitor.cpu-threshold:80}")
    private double cpuThreshold;

    @Getter
    @Value("${server.monitor.memory-threshold:80}")
    private double memoryThreshold;

    private HardwareAbstractionLayer hardware;

    @PostConstruct
    public void init() {
        // 根据操作系统自动选择实现
        this.hardware = new SystemInfo().getHardware();
    }

    /**
     * 每 5 分钟执行一次监控
     */
    @Scheduled(cron = "0 */5 * * * ?")
//    @Scheduled(fixedDelay = 5000)
    public void monitorServerHealth() {
        try {
            double cpuUsage = getCpuUsage();
            double memoryUsage = getMemoryUsage();

            String monitorLog = String.format(
                    "【服务器监控】时间：%s，CPU 使用率：%.2f%%，内存 使用率：%.2f%%",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd HH:mm:ss")),
                    cpuUsage, memoryUsage);
            log.info(monitorLog);

            // 阈值告警
            if (cpuUsage > cpuThreshold) {
//                log.warn("⚠️【告警】CPU 使用率超过阈值！当前：{:.2f}%，阈值：{:.2f}%", cpuUsage, cpuThreshold);
                log.warn("⚠️【告警】CPU 使用率超过阈值！当前：{}%，阈值：{}%",
                        String.format("%.2f", cpuUsage),
                        String.format("%.2f", cpuThreshold));
            }
            if (memoryUsage > memoryThreshold) {
//                log.warn("⚠️【告警】内存使用率超过阈值！当前：{:.2f}%，阈值：{:.2f}%", memoryUsage, memoryThreshold);
                log.warn("⚠️【告警】内存使用率超过阈值！当前：{}%，阈值：{}%",
                        String.format("%.2f", memoryUsage),
                        String.format("%.2f", memoryThreshold));
            }
        } catch (Exception e) {
            log.error("【服务器监控】执行失败", e);
        }
    }

    /**
     * 获取 CPU 使用率（%）
     */
    public double getCpuUsage() {
        CentralProcessor processor = hardware.getProcessor();
        // 采样 1 秒，返回 0~1，乘以 100 转百分比
        return processor.getSystemCpuLoad(1000) * 100;
    }

    /**
     * 获取内存使用率（%）
     */
    public double getMemoryUsage() {
        GlobalMemory memory = hardware.getMemory();
        long total = memory.getTotal();
        long available = memory.getAvailable();
        return (double) (total - available) / total * 100;
    }

}
