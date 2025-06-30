package com.lawnroad.broadcast.live.dto;

import lombok.Data;

import java.util.List;

@Data
public class VodPreviewResponseDto {
    private List<VodPreviewDto> vods;    // 실제 VOD 리스트
    private int totalCount;              // 전체 VOD 개수
    private int totalPages;              // 전체 페이지 수
    private int page;                    // 현재 페이지
    private int size;                    // 페이지당 사이즈
}
