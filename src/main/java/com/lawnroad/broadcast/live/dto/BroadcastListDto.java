package com.lawnroad.broadcast.live.dto;

import lombok.Data;

import java.util.List;

@Data
public class BroadcastListDto {
    private Long broadcastNo;       // 방송 고유 번호
    private String title;           // 방송 제목
    private String thumbnailPath;   // 썸네일 경로

    private String categoryName;    // 카테고리 이름

    private List<String> keywords;  // 키워드 목록

    private String lawyerName;      // 변호사 이름
    private String profilePath;     // 변호사 프로필
}
