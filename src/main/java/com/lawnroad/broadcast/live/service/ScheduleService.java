package com.lawnroad.broadcast.live.service;

import com.lawnroad.broadcast.live.dto.ScheduleCalendarDto;
import com.lawnroad.broadcast.live.dto.ScheduleDateDto;
import com.lawnroad.broadcast.live.dto.ScheduleRequestDto;
import com.lawnroad.broadcast.live.model.ScheduleVo;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    void registerSchedule(ScheduleRequestDto scheduleRequestDto);
    List<ScheduleDateDto> getSchedulesByDate(LocalDate date);
    List<ScheduleCalendarDto> getSchedulesByMonth(String month);
}
