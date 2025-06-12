package com.lawnroad.broadcast.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreQuestionItem {
    private String nickname;
    private String preQuestionContent;
}
