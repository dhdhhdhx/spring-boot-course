package top.zyp.mp.utils;

import jakarta.annotation.Resource;
import net.datafaker.Faker;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import top.zyp.mp.entity.UserAccount;
import top.zyp.mp.service.UserAccountService;

import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/18
 * @Version: 1.0
 */


@Component
public class DataFakerUtil {

    private static final Faker ZH_FAKER = new Faker(Locale.CHINA);
    private static final Faker EN_FAKER = new Faker(Locale.ENGLISH);
    private static final String RAW_PASSWORD = "";
    private static final String ENCODED_PASSWORD = new BCryptPasswordEncoder().encode(RAW_PASSWORD);

    @Resource
    private UserAccountService userAccountService;

    private UserAccount generateOne(int i) {
        UserAccount user = new UserAccount();
        user.setUsername(EN_FAKER.internet().username());
        user.setPassword(ENCODED_PASSWORD);
        user.setNickname(ZH_FAKER.name().fullName());
        user.setEmail(EN_FAKER.internet().emailAddress());
        user.setPhone(ZH_FAKER.phoneNumber().cellPhone());
        user.setAvatarUrl(ZH_FAKER.avatar().image());
        user.setStatus(1);
        user.setDeleted(0);
        user.setVersion(0);
        return user;
    }

    public void generateBatch() {
        int targetCount = 100;
        List<UserAccount> batch = IntStream.range(0, targetCount)
                .mapToObj(this::generateOne)
                .toList();
        // 每 50 条批量插入一次
        userAccountService.saveBatch(batch, 50);
    }
}
