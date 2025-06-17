package com.lawnroad.reservation.controller;

import com.lawnroad.reservation.dto.ReservationsResponseDTO;
import com.lawnroad.reservation.service.ReservationsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lawyer/{lawyerNo}/reservations")
public class LawyerReservationsController {

    private final ReservationsService service;

    public LawyerReservationsController(ReservationsService service) {
        this.service = service;
    }

    @GetMapping
    public List<ReservationsResponseDTO> getReservationsByLawyer(
            @PathVariable Long lawyerNo
    ) {
        return service.getReservationsByLawyer(lawyerNo);
    }

    @PatchMapping("/{reservationNo}/status")
    public ResponseEntity<Void> closeReservation(
            @PathVariable Long lawyerNo,
            @PathVariable Long reservationNo
    ) {
        // 서비스 계층에서 권한 검사(해당 변호사가 맞는지) 후 상태 변경
        service.changeToClosed(reservationNo);
        return ResponseEntity.noContent().build();
    }
}