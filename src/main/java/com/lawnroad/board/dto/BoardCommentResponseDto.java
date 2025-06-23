package com.lawnroad.board.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardCommentResponseDto {
    private Long commentId;
    private String lawyerName;
    private String lawyerProfileImage;
    private String content;
    private LocalDateTime createdAt;
    private Boolean isSelected;
}
