package com.lawnroad.broadcast.live.dto;

import lombok.Data;

import java.util.List;

@Data
public class KeywordRequestDto {
    private Long broadcastNo;
    private List<String> keywords;
}
