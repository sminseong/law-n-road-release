package com.lawnroad.board.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnswerResponseDto {
    private String lawyerName;
    private String lawyerProfileImage;
    private String content;
    private LocalDateTime createdAt;
}
