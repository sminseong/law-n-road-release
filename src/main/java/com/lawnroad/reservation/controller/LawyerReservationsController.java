package com.lawnroad.reservation.controller;

import com.lawnroad.reservation.dto.ReservationsResponseDTO;
import com.lawnroad.reservation.service.ReservationsService;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import com.lawnroad.common.util.JwtTokenUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lawyer/{lawyerNo}/reservations")
public class LawyerReservationsController {

    private final ReservationsService service;
    private final JwtTokenUtil jwtTokenUtil;

    public LawyerReservationsController(ReservationsService service,JwtTokenUtil jwtTokenUtil) {
        this.service = service;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @GetMapping
    public List<ReservationsResponseDTO> getReservationsByLawyer(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtTokenUtil.parseToken(token);
        long no = claims.get("no", Long.class);

        return service.getReservationsByLawyer(no);
    }
    

    @PatchMapping("/{reservationNo}/status")
    public ResponseEntity<Void> closeReservation(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long reservationNo
    ) {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtTokenUtil.parseToken(token);
        long no = claims.get("no", Long.class);

        // 서비스 계층에서 권한 검사(해당 변호사가 맞는지) 후 상태 변경
        service.changeToClosed(reservationNo);
        return ResponseEntity.noContent().build();
    }
}