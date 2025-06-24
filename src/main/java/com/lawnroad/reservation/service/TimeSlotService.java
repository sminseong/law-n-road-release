package com.lawnroad.reservation.service;

import com.lawnroad.reservation.dto.TimeSlotResponseDTO;
import com.lawnroad.reservation.mapper.TimeSlotMapper;
import com.lawnroad.reservation.model.TimeSlotVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class TimeSlotService {

    private static final Logger log = LoggerFactory.getLogger(TimeSlotService.class);

    private final TimeSlotMapper timeSlotMapper;

    public TimeSlotService(TimeSlotMapper timeSlotMapper) {
        this.timeSlotMapper = timeSlotMapper;
    }

    @Transactional
    public void generateWeeklyTimeSlots(Long userNo, LocalDate startDate) {
        Long baseAmount = timeSlotMapper.getPrice(userNo);

        IntStream.range(0, 7).forEach(dayOffset -> {
            LocalDate date = startDate.plusDays(dayOffset);
            IntStream.rangeClosed(8, 22).forEach(hour -> {
                TimeSlotVO vo = new TimeSlotVO(
                        null, userNo, date, LocalTime.of(hour, 0), 0, baseAmount
                );
                timeSlotMapper.insertTimeSlot(vo);
            });
        });
    }

    @Transactional
    public void updateSlotStatuses(List<TimeSlotVO> timeSlotVOList) {
        for (TimeSlotVO vo : timeSlotVOList) {
            // 1) 먼저 넘어오는 VO 값 찍어보기
            log.debug("[DEBUG] incoming VO → slotNo={}, slotDate={}, slotTime={}, newStatus={}",
                    vo.getNo(), vo.getSlotDate(), vo.getSlotTime(), vo.getStatus());

            // 2) “열기(1) 시도” 일 때만 DB count 조회
            if (vo.getStatus() == 1) {
                int cnt = timeSlotMapper.countByLawyerAndSlotAndStatus(vo.getNo());
                // 3) 실제 DB에 몇 건 있는지 찍어보기
                log.debug("[DEBUG] DB reservation count for slotNo={} = {}", vo.getNo(), cnt);

                if (cnt > 0) {
                    throw new IllegalStateException(
                            String.format("%s %s 슬롯에 이미 %d개의 예약 요청이 있어 열 수 없습니다.",
                                    vo.getSlotDate(), vo.getSlotTime(), cnt)
                    );
                }
            }
        }

        // 검사 통과한 VO 만 업데이트
        timeSlotVOList.forEach(timeSlotMapper::updateStatus);
        log.debug("[DEBUG] updateSlotStatuses 완료: {} 건 처리", timeSlotVOList.size());
    }

    @Transactional(readOnly = true)
    public List<TimeSlotResponseDTO> getWeeklyTimeSlots(Long lawyerNo, LocalDate startDate) {
        LocalDate endDate = startDate.plusDays(6);
        List<TimeSlotVO> voList = timeSlotMapper.getWeeklyTimeSlots(lawyerNo, startDate, endDate);

        return voList.stream()
                .map(vo -> {
                    TimeSlotResponseDTO dto = new TimeSlotResponseDTO();
                    BeanUtils.copyProperties(vo, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
