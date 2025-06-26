package com.lawnroad.broadcast.live.controller;

import com.lawnroad.broadcast.live.service.KeywordService;
import com.lawnroad.common.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client/keyword-alert")
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/apply")
    public ResponseEntity<?> registerKeywordAlert(@RequestParam String keyword,
                                                  @RequestHeader("Authorization") String authHeader) {
        try {
            // JWT 토큰에서 userNo 추출
            String token = authHeader.replace("Bearer ", "");
            Claims claims = jwtTokenUtil.parseToken(token);
            Long userNo = claims.get("no", Long.class);

            // 알림 등록 로직 실행
            keywordService.registerKeywordAlert(userNo, keyword);
            return ResponseEntity.ok("알림 신청 완료");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류로 알림 신청에 실패했습니다.");
        }
    }
}
