package top.zyp.mp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("top.zyp.mp.mapper")
public class SpringBootMpApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootMpApplication.class, args);
    }

}
