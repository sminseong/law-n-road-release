package com.lawnroad.broadcast.live.controller;

import com.lawnroad.broadcast.live.dto.BroadcastStartResponseDto;
import com.lawnroad.broadcast.live.service.BroadcastService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/client/broadcast")
@RequiredArgsConstructor
public class BroadcastClientController {

    private final BroadcastService broadcastService;

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
}
