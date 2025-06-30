package com.lawnroad.broadcast.live.dto;

import lombok.Data;

@Data
public class KeywordResponseDto {
    private String keyword; // 프론트에선 방송당 키워드 리스트만 받으면 되므로 단일 키워드로 구성
}
