package com.lawnroad;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@MapperScan("com.lawnroad.template.mapper")
@SpringBootApplication
@MapperScan("com.lawnroad.account.mapper")
public class LawNRoadApplication {
    public static void main(String[] args) {
        SpringApplication.run(LawNRoadApplication.class, args);
    }
}
