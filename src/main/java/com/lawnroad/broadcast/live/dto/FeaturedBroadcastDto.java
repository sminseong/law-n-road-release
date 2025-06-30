package com.lawnroad.broadcast.live.dto;

import lombok.Data;

import java.util.List;

@Data
public class FeaturedBroadcastDto {
    private Long broadcastNo;         // 방송 번호
    private Long scheduleNo;          // 스케줄 번호 (키워드 조회용)
    private String sessionId;         // OpenVidu 세션 ID
    private String title;             // 방송 제목
    private String lawyerName;        // 변호사 이름
    private String lawyerProfile;     // 변호사 프로필 이미지 경로
    private String categoryName;      // 카테고리 이름
    private String thumbnailPath;     // 썸네일 경로
    private List<String> keywords;    // 키워드 목록
}
