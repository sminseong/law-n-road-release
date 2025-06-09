package com.lawnroad.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@Configuration
@EnableAutoConfiguration
@ComponentScan("com.lawnroad.reservation")
@MapperScan(
        basePackages = "com.lawnroad.reservation.mapper",
        annotationClass = org.apache.ibatis.annotations.Mapper.class
)
public class TestConfig {
}
