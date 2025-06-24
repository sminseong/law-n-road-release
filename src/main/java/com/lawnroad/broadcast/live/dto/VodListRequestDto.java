package com.lawnroad.broadcast.live.dto;

import lombok.Data;

@Data
public class VodListRequestDto {
    private int page = 1;
    private int size = 12;

    public int getOffset() {
        return (page - 1) * size;
    }
}
