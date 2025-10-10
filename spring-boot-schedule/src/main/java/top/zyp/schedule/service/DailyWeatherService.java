package top.zyp.schedule.service;

/**
 * @Author: calm_sunset
 * @Date: 2025/10/10
 * @Version: 1.0
 */

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 每日天气早报服务
 */
@Slf4j
@Service
public class DailyWeatherService {

    private final OkHttpClient okHttpClient = new OkHttpClient();

    @Resource
    private JavaMailSender mailSender;

    /* ---------- 配置注入 ---------- */
    @Value("${weather.api.key}")
    private String weatherApiKey;

    @Value("${weather.api.city}")
    private String cityId;

    @Value("${weather.api.url}")
    private String weatherApiUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${email.recipients}")
    private String[] toEmails;

    /* ---------- 定时任务：每天 07:20 执行 ---------- */
    @Scheduled(cron = "30 32 13 * * ?")
    public void sendDailyWeather() {
        try {
            // 1. 获取天气数据
            String weatherResponse = getWeatherData();
            if (weatherResponse == null) {
                log.error("【天气早报】获取天气数据失败");
                return;
            }

            // 2. 解析当天天气
            JSONObject weatherJson = JSONObject.parseObject(weatherResponse);
            JSONObject today = weatherJson.getJSONArray("daily").getJSONObject(0);

            String date = today.getString("fxDate");
            String tempMax = today.getString("tempMax");
            String tempMin = today.getString("tempMin");
            String textDay = today.getString("textDay");
            String windDirDay = today.getString("windDirDay");
            String windScaleDay = today.getString("windScaleDay");

            // 3. 构造邮件
            String subject = String.format("【每日天气早报】%s 南京天气",
                    LocalDate.parse(date).format(DateTimeFormatter.ofPattern("MM月dd日")));
            String content = String.format("""
                    日期：%s
                    天气：%s
                    温度：%s℃ ~ %s℃
                    风向：%s
                    风力：%s级
                    提示：出门请根据天气增减衣物，注意交通安全！
                    """, date, textDay, tempMin, tempMax, windDirDay, windScaleDay);

            // 4. 发送
            sendEmail(subject, content);
            log.info("【天气早报】邮件发送成功，收件人：{}", String.join(",", toEmails));

        } catch (Exception e) {
            log.error("【天气早报】执行失败", e);
        }
    }

    /* ---------- 调用和风天气 API ---------- */
    private String getWeatherData() throws IOException {
        String requestUrl = String.format("%s?location=%s&key=%s", weatherApiUrl, cityId, weatherApiKey);
        Request request = new Request.Builder().url(requestUrl).build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            }
        }
        return null;
    }

    /* ---------- 发送邮件 ---------- */
    private void sendEmail(String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmails);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }
}
