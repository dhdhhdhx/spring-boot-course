package top.zyp.boot.config.model;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Slf4j
class TeamTest {
    @Resource
    private Team team;

    @Test
    void testTeam() {
        log.info("team: {}", team);
    }

    @Test
    void testTeam2() {
        assertEquals("ypzhou",team.getLeader());
    }
    @Test
    void testTeam3() {
        assertEquals("ypzhou213",team.getLeader());
    }
}