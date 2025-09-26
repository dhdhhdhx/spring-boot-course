package top.zyp.filter.interceptor.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/26
 * @Version: 1.0
 */


@Configuration
public class RedisConfig {

    /**
     * 配置RedisTemplate
     * 使用FastJson2作为序列化器，提高性能并保证序列化的可读性
     *
     * @param factory Redis连接工厂
     * @return 配置好的RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 设置key的序列化器为String序列化器，这样Redis中的key就是可读的字符串形式
        template.setKeySerializer(RedisSerializer.string());
        // 设置Hash类型的key序列化器为String序列化器
        template.setHashKeySerializer(RedisSerializer.string());
        // 创建FastJson2序列化器实例
        FastJsonRedisSerializer<Object> serializer = new FastJsonRedisSerializer<>(Object.class);
        // 设置value的序列化器为FastJson2序列化器，支持Java对象与JSON的高效转换
        template.setValueSerializer(serializer);
        // 设置Hash类型的value序列化器
        template.setHashValueSerializer(serializer);
        // 设置Redis连接工厂
        template.setConnectionFactory(factory);
        return template;
    }
}
