package com.lawnroad.broadcast.live.dto;

import lombok.Data;

@Data
public class VodPreviewRequestDto {
    private int page = 1;
    private int size = 4; // 변호사 홈페이지에서는 한 줄만 보여주기

    public int getOffset() {
        return (page - 1) * size;
    }
}
