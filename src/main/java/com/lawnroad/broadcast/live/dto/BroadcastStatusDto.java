package com.lawnroad.broadcast.live.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastStatusDto {
    private Long broadcastNo;
    private String status;
}
