package com.lawnroad.reservation.controller;

import com.lawnroad.reservation.dto.TimeSlotResponseDTO;
import com.lawnroad.reservation.dto.TimeSlotUpdateRequestDTO;
import com.lawnroad.reservation.model.TimeSlotVO;
import com.lawnroad.reservation.service.TimeSlotService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lawyers/{lawyerNo}/slots")
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    public TimeSlotController(TimeSlotService timeSlotService) {
        this.timeSlotService = timeSlotService;
    }

    // 해당 변호사의 주간 슬롯 조회
    @GetMapping
    public List<TimeSlotResponseDTO> getWeeklySlots(
            @PathVariable Long lawyerNo,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate
    ) {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        return timeSlotService.getWeeklyTimeSlots(lawyerNo, startDate);
    }

    // 해당 변호사의 주간 슬롯 변경
    @PutMapping
    public ResponseEntity<Void> updateWeeklySlots(
            @PathVariable Long lawyerNo,
            @RequestBody List<TimeSlotUpdateRequestDTO> updates
    ) {
        List<TimeSlotVO> vos = updates.stream()
                .map(r -> new TimeSlotVO(
                        null,
                        lawyerNo,
                        r.getSlotDate(),
                        r.getSlotTime(),
                        r.getStatus(),
                        null
                ))
                .collect(Collectors.toList());

        timeSlotService.updateSlotStatuses(vos);
        return ResponseEntity.noContent().build();
    }
}
