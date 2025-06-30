package com.lawnroad.broadcast.live.dto;

import lombok.Data;

import java.util.List;

@Data
public class BroadcastListResponseDto {
    private List<BroadcastListDto> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;
}
