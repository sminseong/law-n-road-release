package com.lawnroad.reservation.controller;

import com.lawnroad.common.util.JwtTokenUtil;
import com.lawnroad.reservation.dto.ReservationCountDTO;
import com.lawnroad.reservation.dto.ReservationsCreateDTO;
import com.lawnroad.reservation.dto.ReservationsResponseDTO;
import com.lawnroad.reservation.service.ReservationsService;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client/reservations")
public class ClientReservationsController {

    private final ReservationsService service;
    private final JwtTokenUtil jwtTokenUtil;

    public ClientReservationsController(ReservationsService service, JwtTokenUtil jwtTokenUtil) {
        this.service = service;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    /** 1) 예약 신청 */
    @PostMapping
    public ResponseEntity<ReservationsResponseDTO> createReservation(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ReservationsCreateDTO dto
    ) {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtTokenUtil.parseToken(token);
        long no = claims.get("no", Long.class);
        dto.setUserNo(no);
        Long reservationNo = service.createReservation(dto);

        // 바로 상세 조회해서 응답
        ReservationsResponseDTO responseDto =
                service.getReservationByNo(reservationNo);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }

    /** 2) 내 예약 리스트 조회 */
    @GetMapping("/list")
    public ResponseEntity<List<ReservationsResponseDTO>> getMyReservations(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtTokenUtil.parseToken(token);
        long no = claims.get("no", Long.class);

        List<ReservationsResponseDTO> list =
                service.getReservationsByUser(no);
        return ResponseEntity.ok(list);
    }

    /** 3) 내 예약 상태 카운트 */
    @GetMapping("/counts")
    public ResponseEntity<ReservationCountDTO> getCounts(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtTokenUtil.parseToken(token);
        long no = claims.get("no", Long.class);

        ReservationCountDTO counts =
                service.countByStatus(no);
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/{reservationNo}")
    public ResponseEntity<ReservationsResponseDTO> detail(
            @PathVariable Long reservationNo,
            @RequestHeader("Authorization") String authHeader

    ) {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtTokenUtil.parseToken(token);
        long no = claims.get("no", Long.class);

        // 2) 실제 예약 정보 가져오기
        ReservationsResponseDTO dto = service.getReservationDetail(reservationNo);

        // 3) 소유권 검증: userNo가 일치하지 않으면 403
        if (!dto.getUserNo().equals(no)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }
        return ResponseEntity.ok(dto);
    }
}
