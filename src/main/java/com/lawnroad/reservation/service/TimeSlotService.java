package com.lawnroad.reservation.service;

import com.lawnroad.reservation.mapper.TimeSlotMapper;
import com.lawnroad.reservation.model.TimeSlotVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.IntStream;

//회원가입 성공 시 일주일 치의 컬럼 생성 하는 서비스
//회원가입 컨트롤러에서 해당 기능 수행 예정
@Service
public class TimeSlotService {

    private final TimeSlotMapper timeSlotMapper;

    public TimeSlotService(TimeSlotMapper timeSlotMapper) {
        this.timeSlotMapper = timeSlotMapper;
    }
    @Transactional
    public void generateWeeklyTimeSlots(Long userNo, LocalDate startDate) {
        Long baseAmount = timeSlotMapper.getPrice(userNo);

        IntStream.range(0, 7).forEach(offset -> {
            LocalDate targetDate = startDate.plusDays(offset);

            // 하루에 08시부터 22시까지 1시간 단위로 루프
            IntStream.rangeClosed(8, 22).forEach(hour -> {
                TimeSlotVO vo = new TimeSlotVO(
                        null,
                        userNo,
                        targetDate,
                        LocalTime.of(hour, 0),
                        0,
                        baseAmount
                );
                timeSlotMapper.insertTimeSlot(vo);
            });
        });
    }
}