package com.lawnroad.broadcast.live.service;

import com.lawnroad.broadcast.live.dto.ScheduleRequestDto;
import com.lawnroad.broadcast.live.mapper.ScheduleMapper;
import com.lawnroad.broadcast.live.model.ScheduleVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleMapper scheduleMapper;

    @Override
    public void registerSchedule(ScheduleRequestDto scheduleRequestDto) {
        ScheduleVo scheduleVo = ScheduleVo.builder()
                .userNo(scheduleRequestDto.getUserNo())
                .categoryNo(scheduleRequestDto.getCategoryNo())
                .name(scheduleRequestDto.getName())
                .content(scheduleRequestDto.getContent())
                .thumbnailPath(scheduleRequestDto.getThumbnailPath())
                .date(scheduleRequestDto.getDate())
                .startTime(scheduleRequestDto.getStartTime())
                .endTime(scheduleRequestDto.getEndTime())
                .build();
    }
}
