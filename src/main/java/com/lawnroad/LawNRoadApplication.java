package com.lawnroad;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@MapperScan({"com.lawnroad.template.mapper", "com.lawnroad.account.mapper", "com.lawnroad.board.mapper", "com.lawnroad.mainsearch.mapper", "com.lawnroad.advertisement.mapper"})
public class LawNRoadApplication {
    public static void main(String[] args) {

        //PasswordEncoder encoder = new BCryptPasswordEncoder();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin123";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("Encoded password: " + encodedPassword);
        System.out.println("$2a$10$vPt7kcx0P0rAlWJpsqgK1..0bY8bRP60wYUwvkxgbfsNCou9hQkT2");






        SpringApplication.run(LawNRoadApplication.class, args);
    }
}