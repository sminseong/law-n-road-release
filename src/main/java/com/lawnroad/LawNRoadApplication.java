package com.lawnroad;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"com.lawnroad.template.mapper", "com.lawnroad.account.mapper", "com.lawnroad.board.mapper"})
public class LawNRoadApplication {
    public static void main(String[] args) {
        SpringApplication.run(LawNRoadApplication.class, args);
    }
}
