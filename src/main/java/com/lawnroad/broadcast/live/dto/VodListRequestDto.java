package com.lawnroad.broadcast.live.dto;

import lombok.Data;

@Data
public class VodListRequestDto {
    private int page = 1;
    private int size = 12;
    private String sort = "recent"; // "recent" or "popular"
    private Long categoryNo; // 선택된 카테고리 번호 (nullable)

    public int getOffset() {
        return (page - 1) * size;
    }
}
