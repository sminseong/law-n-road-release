package com.lawnroad.broadcast.live.dto;

import lombok.Data;

import java.util.List;

@Data
public class VodListResponseDto {
    private List<VodListDto> content;
    private int totalPages;
    private long totalElements;
    private int currentPage;
}
