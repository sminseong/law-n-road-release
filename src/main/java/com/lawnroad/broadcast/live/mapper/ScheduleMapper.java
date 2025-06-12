package com.lawnroad.broadcast.live.mapper;

import com.lawnroad.broadcast.live.dto.*;
import com.lawnroad.broadcast.live.model.ScheduleVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ScheduleMapper {
    // 스케줄 등록(insert)
    void insertSchedule(ScheduleVo scheduleVo);
    List<ScheduleDateDto> findAllByDate(@Param("date") LocalDate date);
    List<ScheduleCalendarDto> findAllByMonth(@Param("month") String month);
    List<ScheduleResponseDto> findAllByLawyer(@Param("userNo") Long userNo);
    ScheduleDetailDto findByScheduleNo(@Param("scheduleNo") Long scheduleNo);

    void updateSchedule(ScheduleUpdateDto scheduleUpdateDto);
}
