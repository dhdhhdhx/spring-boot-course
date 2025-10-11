package top.zyp.websocket.task;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.zyp.websocket.handler.WeatherWebSocketHandler;
import top.zyp.websocket.service.DailyWeatherService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherWebSocketTask {

    private final DailyWeatherService weatherService;
    private final WeatherWebSocketHandler handler;

    /**
     * 每天 07:20 推送天气早报
     */
//    @Scheduled(cron = "0 20 7 * * ?")
    @Scheduled(cron = "0 */1 * * * ?")
    public void pushDailyWeather() {
        try {
            String resp = weatherService.getWeatherData();
            if (resp == null) {
                log.error("[WS-天气] 获取数据失败");
                return;
            }

            JSONObject root = JSONObject.parseObject(resp);
            JSONArray forecasts = root.getJSONArray("forecasts");
            if (forecasts == null || forecasts.isEmpty()) return;

            JSONObject today = forecasts.getJSONObject(0)
                    .getJSONArray("casts")
                    .getJSONObject(0);

            String date      = today.getString("date");
            String textDay   = today.getString("dayweather");
            String tempMax   = today.getString("daytemp");
            String tempMin   = today.getString("nighttemp");
            String windDir   = today.getString("daywind");
            String windScale = today.getString("daypower");

            String pushText = String.format(
                    "📅 日期：%s\n🌤️ 天气：%s\n🌡️ 温度：%s℃ ~ %s℃\n💨 风向：%s\n🌬️ 风力：%s级\n💡 提示：出门请根据天气增减衣物，注意交通安全！",
                    LocalDate.parse(date).format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")),
                    textDay, tempMin, tempMax, windDir, windScale
            );

            handler.broadcast(pushText);
            log.info("[WS-天气] 推送完成，在线客户端：{}", WeatherWebSocketHandler.SESSIONS.size());

        } catch (Exception e) {
            log.error("[WS-天气] 推送异常", e);
        }
    }
}