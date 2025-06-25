package com.lawnroad;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableScheduling
@SpringBootApplication
@MapperScan({
        "com.lawnroad.broadcast.live.mapper",
        "com.lawnroad.broadcast.chat.mapper",
        "com.lawnroad.template.mapper",
        "com.lawnroad.account.mapper",
        "com.lawnroad.board.mapper",
        "com.lawnroad.mainsearch.mapper",
        "com.lawnroad.advertisement.mapper",
        "com.lawnroad.admin.mapper",
        "com.lawnroad.keyword.mapper"
})
public class LawNRoadApplication {
    public static void main(String[] args) {


        SpringApplication.run(LawNRoadApplication.class, args);
    }
}