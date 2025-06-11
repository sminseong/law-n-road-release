package com.lawnroad;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@MapperScan("com.lawnroad.template.mapper")
@SpringBootApplication
@MapperScan("com.lawnroad.account.mapper")
@MapperScan("com.lawnroad.broadcast.live.mapper")
@MapperScan("com.lawnroad.broadcast.chat.mapper") // 패키지에 맞게!
public class LawNRoadApplication {
    public static void main(String[] args) {
        SpringApplication.run(LawNRoadApplication.class, args);
    }
}
