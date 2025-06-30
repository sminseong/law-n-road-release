package com.lawnroad.broadcast.live.service;

import com.lawnroad.broadcast.live.dto.*;
import com.lawnroad.broadcast.live.model.ScheduleVo;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    void registerSchedule(ScheduleRequestDto scheduleRequestDto);
    List<ScheduleDateDto> getSchedulesByDate(LocalDate date);
    List<ScheduleCalendarDto> getSchedulesByMonth(String month);
    List<ScheduleResponseDto> getSchedulesByLawyer(Long userNo);
    ScheduleDetailDto findDetailByScheduleNo(Long scheduleNo);
    void updateSchedule(ScheduleUpdateDto scheduleUpdateDto);
    int deleteScheduleByNo(Long scheduleNo);
}
