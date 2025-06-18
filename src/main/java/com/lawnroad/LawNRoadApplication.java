package com.lawnroad;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@MapperScan({"com.lawnroad.template.mapper", "com.lawnroad.account.mapper", "com.lawnroad.board.mapper"})
public class LawNRoadApplication {
    public static void main(String[] args) {

        PasswordEncoder encoder = new BCryptPasswordEncoder();

        String raw = "!tlqk"; // 로그인 시 입력한 비번
        String hashed = "$2a$10$OPqvUfC54R/PlyCYBaZdjeJgCS3NTxz4F2jyCyhAf8AQ/3bAMNTeK"; // DB 값

        System.out.println("일치? " + encoder.matches(raw, hashed)); // true or false


        SpringApplication.run(LawNRoadApplication.class, args);
    }
}