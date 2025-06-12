package com.lawnroad.broadcast.chat.service;

import com.lawnroad.broadcast.chat.dto.PreQuestionDTO;
import com.lawnroad.broadcast.chat.mapper.PreQuestionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PreQuestionServiceImpl implements PreQuestionService {

    private final PreQuestionMapper preQuestionMapper;


    @Override
    public List<PreQuestionDTO> findByPreQuestion(int scheduleNo) {
        return preQuestionMapper.findByPreQuestion(scheduleNo);
    }



}
