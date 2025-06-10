package com.lawnroad.broadcast.live.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CategoryVo {
    private Long no;
    private String name;
    private LocalDateTime createdAt;
}
