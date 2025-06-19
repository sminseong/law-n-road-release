package com.lawnroad.account.controller;


import com.lawnroad.account.entity.ClientEntity;
import com.lawnroad.account.entity.LawyerEntity;
import com.lawnroad.account.mapper.ClientMapper;
import com.lawnroad.account.mapper.LawyerMapper;
import com.lawnroad.common.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final JwtTokenUtil jwtUtil;
    private final ClientMapper clientMapper;  // ✅ MyBatis Mapper 주입
    private final LawyerMapper lawyerMapper;

    @GetMapping("/mypage")
    public ResponseEntity<?> getUserMypage(@RequestHeader("Authorization") String authHeader) {
        try {
            // 1. 토큰에서 clientId, nickname 추출
            String token = authHeader.replace("Bearer ", "");
            Claims claims = jwtUtil.parseToken(token);

            String clientId = claims.getSubject(); // sub → clientId
            String nickname = claims.get("nickname", String.class);
            long no = claims.get("no", Long.class);
            System.out.println(no);


            // 2. clientMapper로 조회
            ClientEntity client = clientMapper.findByClientId(clientId); // ✅ MyBatis 메서드 사용
            if (client == null) {
                return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
            }

            // 3. 응답 반환
            return ResponseEntity.ok(Map.of(
                    "clientId", client.getClientId(),
                    "nickname", nickname,
                    "userNo", client.getNo()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(401).body("토큰 파싱 실패: " + e.getMessage());
        }
    }


    @GetMapping("mypageLawyer")
    public ResponseEntity<?> getLawyerMypage(@RequestHeader("Authorization") String authHeader) {
        try {
            // 1. 토큰에서 clientId, nickname 추출
            String token = authHeader.replace("Bearer ", "");
            Claims claims = jwtUtil.parseToken(token);

            //String lawyerId = claims.get("lawyerId", String.class); // sub → clientId
            //String nickname = claims.get("nickname", String.class);
            String name = claims.get("name", String.class);


            // 2. clientMapper로 조회
            LawyerEntity lawyer = lawyerMapper.findByLawyerId(name); // ✅ MyBatis 메서드 사용
            if (lawyer == null) {
                return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
            }

            // 3. 응답 반환
            return ResponseEntity.ok(Map.of(
                    "lawyerId", lawyer.getLawyerId(),
                    "name", name,
                    "userNo", lawyer.getNo()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(401).body("토큰 파싱 실패: " + e.getMessage());
        }
    }


}
