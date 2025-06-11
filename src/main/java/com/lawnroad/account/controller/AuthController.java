package com.lawnroad.account.controller;


import com.lawnroad.account.dto.ClientSignupRequest;
import com.lawnroad.account.dto.LoginRequest;
import com.lawnroad.account.dto.LoginResponseDto;
import com.lawnroad.account.entity.ClientEntity;
import com.lawnroad.account.service.ClientService;
import com.lawnroad.common.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private ClientService clientService;
    private final JwtTokenUtil jwtTokenUtil;

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);





    @GetMapping("/check-id")
    public ResponseEntity<Map<String, Object>> checkIdDuplicate(@RequestParam String client_id) {
        boolean available = clientService.isClientIdAvailable(client_id);

        Map<String, Object> response = new HashMap<>();
        response.put("available", available);

        return ResponseEntity.ok(response); // 요청 성공했고, 이 데이터 줄게!  200일 때만 적용됨
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<Map<String, Object>> checkNickNameDuplicate(@RequestParam String nickname) {
        boolean available = clientService.isClientNickNameAvailable(nickname);

        Map<String, Object> response = new HashMap<>();
        response.put("available", available);

        return ResponseEntity.ok(response); // 요청 성공했고, 이 데이터 줄게!  200일 때만 적용됨
    }


    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody ClientSignupRequest request) {
        clientService.registerClient(request);
        return ResponseEntity.ok().body("회원가입 완료");
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmailDuplicate(@RequestParam String email) {
        boolean available = clientService.isEmailAvailable(email);

        Map<String, Object> response = new HashMap<>();
        response.put("available", available);

        return ResponseEntity.ok(response);
    }



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
//        try {
//            if (!"client".equals(request.getType())) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("지원되지 않는 로그인 유형입니다.");
//            }
//
//            ClientEntity client = clientService.login(request.getEmail(), request.getPassword());
//
//            Map<String, String> result = new HashMap<>();
//
//            result.put("name", client.getName());
//            result.put("client_id", client.getClient_id());
//
//            return ResponseEntity.ok(result);
//
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
//        }
//    }
        try {
            ClientEntity client = clientService.login(request.getEmail(), request.getPassword());

            String accessToken = jwtTokenUtil.generateAccessToken(client.getEmail());
            String refreshToken = jwtTokenUtil.generateRefreshToken(client.getEmail());

            System.out.println("✅ Access Token: " + accessToken);
            jwtTokenUtil.printPayload(accessToken);

            // ✅ 확인용 콘솔 출력
            System.out.println("✅ Access Token: " + accessToken);
            System.out.println("🔄 Refresh Token: " + refreshToken);

            // refreshToken은 DB 또는 Redis에 저장 가능
            jwtTokenUtil.storeRefreshToken(client.getEmail(), refreshToken);

            Map<String, Object> result = new HashMap<>();
            result.put("accessToken", accessToken);
            result.put("refreshToken", refreshToken);
            result.put("name", client.getName());
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
        }

}

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> payload) {
        String refreshToken = payload.get("refreshToken");

        if (!jwtTokenUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token 만료");
        }

        String email = jwtTokenUtil.getEmailFromToken(refreshToken);
        if (!jwtTokenUtil.isRefreshTokenValid(email, refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token 불일치");
        }

        String newAccessToken = jwtTokenUtil.generateAccessToken(email);
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }



}
