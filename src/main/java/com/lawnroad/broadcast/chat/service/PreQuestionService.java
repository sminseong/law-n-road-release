package com.lawnroad.broadcast.chat.service;

import com.lawnroad.broadcast.chat.dto.PreQuestionDTO;


public interface PreQuestionService {
    PreQuestionDTO findBySchedule(int scheduleNo);
}
