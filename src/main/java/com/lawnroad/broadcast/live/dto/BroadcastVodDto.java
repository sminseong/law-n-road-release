package com.lawnroad.broadcast.live.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastVodDto {
    private Long no;
    private Long broadcastNo;
    private String vodPath;
    private Integer duration;     // 초 단위
    private Integer status;       // 0 = 유지, 1 = 삭제됨
    private LocalDateTime createdAt;
}
