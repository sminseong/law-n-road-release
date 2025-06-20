package com.lawnroad.board.dto;

import lombok.Data;

@Data
public class CommentResponse {
    private String lawyerName;
    private String lawyerProfileImage;
    private String content;
    private String  createdAt;
}
