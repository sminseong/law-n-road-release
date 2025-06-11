package com.lawnroad.broadcast.chat.dto;

import lombok.*;


import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatReportDTO {
    private Long UserNo;
    private String nickname;
    private String message;
    private LocalDateTime CreatedAt;
}
