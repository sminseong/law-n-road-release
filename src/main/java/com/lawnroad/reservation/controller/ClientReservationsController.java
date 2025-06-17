package com.lawnroad.reservation.controller;

import com.lawnroad.reservation.dto.ReservationCountDTO;
import com.lawnroad.reservation.dto.ReservationsCreateDTO;
import com.lawnroad.reservation.dto.ReservationsResponseDTO;
import com.lawnroad.reservation.service.ReservationsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client/{userNo}/reservations")
public class ClientReservationsController {

    private final ReservationsService service;

    public ClientReservationsController(ReservationsService service) {
        this.service = service;
    }

    /** 1) 예약 신청 */
    @PostMapping
    public ResponseEntity<ReservationsResponseDTO> createReservation(
            @PathVariable Long userNo,
            @RequestBody ReservationsCreateDTO dto
    ) {
        dto.setUserNo(userNo);
        Long reservationNo = service.createReservation(dto);

        // 바로 상세 조회해서 응답
        ReservationsResponseDTO responseDto =
                service.getReservationByNo(reservationNo);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }

    /** 2) 내 예약 리스트 조회 */
    @GetMapping
    public ResponseEntity<List<ReservationsResponseDTO>> getMyReservations(
            @PathVariable Long userNo
    ) {
        List<ReservationsResponseDTO> list =
                service.getReservationsByUser(userNo);
        return ResponseEntity.ok(list);
    }

    /** 3) 내 예약 상태 카운트 */
    @GetMapping("/counts")
    public ResponseEntity<ReservationCountDTO> getCounts(
            @PathVariable Long userNo
    ) {
        ReservationCountDTO counts =
                service.countByStatus(userNo);
        return ResponseEntity.ok(counts);
    }
}
