package com.lawnroad.broadcast.chat.dto;

import lombok.*;


import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatReportDTO {
    private Long userNo;
    private String nickname;
    private String message;
    private LocalDateTime CreatedAt;
}
