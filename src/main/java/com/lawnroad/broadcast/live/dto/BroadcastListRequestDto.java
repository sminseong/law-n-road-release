package com.lawnroad.broadcast.live.dto;

import lombok.Data;

@Data
public class BroadcastListRequestDto {
    private int page = 1;   // 기본값: 1페이지
    private int size = 9;   // 기본값: 9개씩 (3줄)

    public int getOffset() {
        return (page - 1) * size;
    }
}
