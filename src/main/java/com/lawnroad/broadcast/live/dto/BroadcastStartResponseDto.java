package com.lawnroad.broadcast.live.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastStartResponseDto {
    private String sessionId;
    private String token;
}
