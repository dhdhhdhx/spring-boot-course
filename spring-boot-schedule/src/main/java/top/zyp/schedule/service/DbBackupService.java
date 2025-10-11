package top.zyp.schedule.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import top.zyp.schedule.config.DbConfig;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @Author: calm_sunset
 * @Date: 2025/10/10
 * @Version: 1.0
 */



@Slf4j
@Service
@AllArgsConstructor
public class DbBackupService {
    private final DbConfig dbConfig;


    /**
     * 每日凌晨2:00执行备份
     */
    @Scheduled(cron = "10 15 12 * * ?")
//    @Scheduled(cron = "*/10 * * * * ?") // 每 10 秒执行一次
    public void backupWithHutool() {
        String host = dbConfig.getHost();
        String user = dbConfig.getUsername();
        String password = dbConfig.getPassword();
        String database = dbConfig.getDbName();
        String backupFile = "D:/dbBackup/" + database + "_" + System.currentTimeMillis() + ".sql";

        /* 1. 目录必须存在 */
        try {
            Files.createDirectories(Paths.get(backupFile).getParent());
        } catch (IOException e) {
            log.error("创建备份目录失败", e);
            return;
        }

        /* 2. 构造命令  mysqldump -h host -u user -ppassword database */
        CommandLine cmd = new CommandLine("D:/mysql-8.0.42-winx64/bin/mysqldump.exe");
        cmd.addArgument("-h").addArgument(host);
        cmd.addArgument("-u").addArgument(user);
        cmd.addArgument("-p" + password);   // 紧贴，无空格
        cmd.addArgument(database);          // 数据库名放最后

        /* 3. 重定向 stdout 到文件 */
        DefaultExecutor executor = new DefaultExecutor();
        try (FileOutputStream fos = new FileOutputStream(backupFile)) {
            executor.setStreamHandler(new PumpStreamHandler(fos, System.err));
            int exit = executor.execute(cmd);
            if (exit == 0) {
                log.info("数据库备份成功，文件：{}", backupFile);
            } else {
                log.error("备份失败，退出码：{}", exit);
            }
        } catch (IOException e) {
            log.error("备份异常", e);
        }
    }
}