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

    @PostMapping
    public ResponseEntity<Void> createReservation(
            @PathVariable Long userNo,
            @RequestBody ReservationsCreateDTO dto
    ) {
        dto.setUserNo(userNo);
        service.createReservation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public List<ReservationsResponseDTO> getReservationsByUser(
            @PathVariable Long userNo
    ) {
        return service.getReservationsByUser(userNo);
    }

    @GetMapping("/counts")
    public ReservationCountDTO getCounts(@PathVariable Long userNo) {
        return service.countByStatus(userNo);
    }
}
