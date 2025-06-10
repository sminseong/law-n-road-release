package com.lawnroad.broadcast.live.service;

import com.lawnroad.broadcast.live.dto.ScheduleRequestDto;
import com.lawnroad.broadcast.live.model.ScheduleVo;

public interface ScheduleService {
    void registerSchedule(ScheduleRequestDto scheduleRequestDto);
}
