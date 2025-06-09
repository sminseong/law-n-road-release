package com.lawnroad.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@Configuration
@EnableAutoConfiguration   // 스프링 부트의 자동 설정(예: DataSource, Transaction 등)을 켬
@ComponentScan(basePackages = "com.lawnroad.reservation.service")
@MapperScan("com.lawnroad.reservation.mapper")
public class TestConfig {
}
