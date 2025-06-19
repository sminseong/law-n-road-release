package com.lawnroad.broadcast.live.controller;

import com.lawnroad.broadcast.live.dto.*;
import com.lawnroad.broadcast.live.mapper.BroadcastMapper;
import com.lawnroad.broadcast.live.service.BroadcastService;
import com.lawnroad.common.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client/broadcast")
@RequiredArgsConstructor
public class BroadcastClientController {

    private final JwtTokenUtil jwtTokenUtil;
    private final BroadcastService broadcastService;
    private final BroadcastMapper broadcastMapper;

    /**
     * 시청자용 방송 토큰 발급 API
     * 프론트에서는 해당 API를 통해 OpenVidu 토큰을 받아서 세션에 접속할 수 있음
     *
     * @param broadcastNo 방송 번호 (PathVariable)
     * @return OpenVidu 토큰 (String)
     */
    @GetMapping("/{broadcastNo}/token")
    public ResponseEntity<BroadcastStartResponseDto> getViewerToken(@PathVariable Long broadcastNo) {
        BroadcastStartResponseDto dto = broadcastService.getClientToken(broadcastNo);
        return ResponseEntity.ok(dto);
    }
    @GetMapping("/view-detail/{broadcastNo}")
    public ResponseEntity<BroadcastViewDetailDto> getDetailByBroadcastNo(@PathVariable Long broadcastNo) {
        return ResponseEntity.ok(broadcastService.getDetailByBroadcastNo(broadcastNo));
    }

    @PostMapping("/report")
    public ResponseEntity<String> reportBroadcast(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody BroadcastReportRequestDto dto
    ) {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtTokenUtil.parseToken(token);
        Long userNo = claims.get("no", Long.class);

        dto.setUserNo(userNo);

        broadcastService.reportBroadcast(dto);
        return ResponseEntity.ok("신고가 정상적으로 접수되었습니다.");
    }

    @GetMapping("/report-reasons")
    public ResponseEntity<List<ReportReasonDto>> getReportReasons() {
        return ResponseEntity.ok(broadcastMapper.findAllReportReasons());
    }
    // 라이브 목록조회
    @GetMapping("/live")
    public ResponseEntity<List<BroadcastListDto>> getLiveBroadcasts() {
        List<BroadcastListDto> liveList = broadcastService.getLiveBroadcasts();
        return ResponseEntity.ok(liveList);
    }
}
