package com.lawnroad.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {
        "com.lawnroad.reservation.service",
        "com.lawnroad.payment.service"
})
@MapperScan({
        "com.lawnroad.reservation.mapper",
        "com.lawnroad.payment.mapper"
})
public class TestConfig {
}
