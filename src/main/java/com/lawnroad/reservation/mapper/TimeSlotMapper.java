package com.lawnroad.reservation.mapper;

import com.lawnroad.reservation.model.TimeSlotVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TimeSlotMapper {
    void insertTimeSlot(TimeSlotVO timeSlotVO);
    Long getPrice(@Param("userNo") Long userNo);
}
