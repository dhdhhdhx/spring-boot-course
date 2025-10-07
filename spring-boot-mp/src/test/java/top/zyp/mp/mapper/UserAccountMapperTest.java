package top.zyp.mp.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import top.zyp.mp.entity.UserAccount;

@Slf4j
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
class UserAccountMapperTest {
    @Mock
    private UserAccountMapper userAccountMapper;

    @Test
    void add(){
        LambdaQueryWrapper<UserAccount> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserAccount::getUsername, "lijingyao");
        UserAccount user = userAccountMapper.selectOne(lambdaQueryWrapper);
        System.out.println( user);
    }

}