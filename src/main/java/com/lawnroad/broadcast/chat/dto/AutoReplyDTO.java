package com.lawnroad.broadcast.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutoReplyDTO {
    private Long scheduleNo;
    private String keyword;
    private String message;
    private LocalDateTime createdAt;
}
