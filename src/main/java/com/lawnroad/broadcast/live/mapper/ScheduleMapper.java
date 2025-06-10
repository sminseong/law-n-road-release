package com.lawnroad.broadcast.live.mapper;

import com.lawnroad.broadcast.live.model.ScheduleVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ScheduleMapper {
    // 스케줄 등록(insert)
    void insertSchedule(ScheduleVo scheduleVo);
    List<ScheduleVo> findAllByDate(@Param("date") LocalDate date);
}
