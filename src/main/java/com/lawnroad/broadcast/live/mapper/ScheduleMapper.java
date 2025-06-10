package com.lawnroad.broadcast.live.mapper;

import com.lawnroad.broadcast.live.model.ScheduleVo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ScheduleMapper {
    // 스케줄 등록(insert)
    int insertSchedule(ScheduleVo scheduleVo);
}
