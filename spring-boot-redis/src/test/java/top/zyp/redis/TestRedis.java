package top.zyp.redis;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import top.zyp.redis.entity.Address;
import top.zyp.redis.entity.Student;

import java.util.concurrent.TimeUnit;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/25
 * @Version: 1.0
 */

@SpringBootTest
public class TestRedis {
    @Resource
    private RedisTemplate<String, Student> redisTemplate;

//    @Test
//    public void test() {
//        redisTemplate.opsForValue().set("name", "zyp");
//        redisTemplate.opsForValue().set("age", "18",20, TimeUnit.SECONDS);
//    }
    @Test
    void testStudent() {
        Address address = Address.builder().city("上海").province("上海").build();
        Student student = Student.builder().name("zyp").age(18).address(address).build();
        redisTemplate.opsForValue().set("student", student, TimeUnit.SECONDS.ordinal());
    }

}
