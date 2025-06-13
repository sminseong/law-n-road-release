package com.lawnroad.broadcast.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreQuestionDTO {
    private Long categoryNo;
    private String lawyerName;
    private String name;
    private String scheduleContent;
    private String thumbnailPath;
    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<String> keywords;
    private List<PreQuestionItem> preQuestions;
}

