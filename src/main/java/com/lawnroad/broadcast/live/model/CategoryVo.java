package com.lawnroad.broadcast.live.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryVo {
    private Long no;
    private String name;
    private LocalDateTime createdAt;
}
