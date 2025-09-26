package top.zyp.redis.service.impl;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import top.zyp.redis.entity.User;
import top.zyp.redis.mapper.UserMapper;

import java.time.LocalDateTime;

class UserServiceImplTest {
    @Mock
    private UserMapper userAccountMapper;

    @InjectMocks
    private UserServiceImpl userAccountService;

    @Test
    void createUser() {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setAccount("testuser");
        testUser.setPassword("hashedpassword");
        testUser.setNickname("Test User");
        testUser.setPhone("13800138000");
        testUser.setAvatar("https://example.com/avatar.jpg");
        testUser.setDeleted(0);
        testUser.setVersion(1);
        testUser.setCreateTime(LocalDateTime.now());
        testUser.setUpdateTime(LocalDateTime.now());
    }

}