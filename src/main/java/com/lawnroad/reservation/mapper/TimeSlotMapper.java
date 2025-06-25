package com.lawnroad.reservation.mapper;

import com.lawnroad.reservation.model.TimeSlotVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Mapper
public interface TimeSlotMapper {
    // 일주일치 주간 슬롯 생성
    void insertTimeSlot(TimeSlotVO timeSlotVO);
    // 해당 변호사의 상담 가격 가져오기
    Long getPrice(@Param("userNo") Long userNo);
    // 변호사 예약 설정에서 상태 변경
    void updateStatus(TimeSlotVO vo);
    default void updateStatuses(List<TimeSlotVO> vos) {
        for (TimeSlotVO vo : vos) {
            updateStatus(vo);
        }
    }
    // 해당 변호사의 주간 슬롯 모두 조회
    List<TimeSlotVO> getWeeklyTimeSlots(
            @Param("userNo") Long userNo,
            @Param("startDate") LocalDate startDate,
            @Param("endDate")   LocalDate endDate
    );
    
    // 주간 슬롯에서 가격을 가져오기
    Long getAmountBySlotNo(@Param("slotNo") Long slotNo);
    // slotNo로 슬롯 전체 정보(날짜·시간·userNo 등)를 조회
    TimeSlotVO selectBySlotNo(@Param("slotNo") Long slotNo);

    int countByLawyerAndSlotAndStatus(
            @Param("slotNo") Long slotNo
    );
}
