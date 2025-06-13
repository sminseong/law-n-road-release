package com.lawnroad.broadcast.chat.service;


import com.lawnroad.broadcast.chat.dto.PreQuestionDTO;

import java.util.List;

public interface PreQuestionService {
    PreQuestionDTO findByPreQuestion(int scheduleNo);
}
