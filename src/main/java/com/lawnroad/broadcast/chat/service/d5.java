package com.lawnroad.broadcast.chat.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class d5 {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostConstruct
    public void testRedis() {
        redisTemplate.opsForValue().set("check", "ok");
        System.out.println(redisTemplate.opsForValue().get("check")); // → ok
    }

}
