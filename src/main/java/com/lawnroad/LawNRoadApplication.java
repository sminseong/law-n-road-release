package com.lawnroad;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.lawnroad.template.mapper")
@SpringBootApplication
public class LawNRoadApplication {
    public static void main(String[] args) {
        SpringApplication.run(LawNRoadApplication.class, args);
    }
}
