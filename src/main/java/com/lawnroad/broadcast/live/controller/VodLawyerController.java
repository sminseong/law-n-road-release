package com.lawnroad.broadcast.live.controller;

import com.lawnroad.broadcast.live.service.VodService;
import com.lawnroad.common.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/lawyer/vod")
@RequiredArgsConstructor
public class VodLawyerController {

    private final VodService vodService;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/upload/{broadcastNo}")
    public ResponseEntity<?> uploadVod(
            @PathVariable Long broadcastNo,
            @RequestParam("file") MultipartFile file,
            @RequestParam("duration") Integer duration,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Claims claims = jwtTokenUtil.parseToken(token);
            Long userNo = claims.get("no", Long.class);

            vodService.saveVodFile(broadcastNo, file, duration, userNo);
            return ResponseEntity.ok("VOD 업로드 성공");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("VOD 업로드 실패: " + e.getMessage());
        }
    }
}
