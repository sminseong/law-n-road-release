package com.lawnroad.account.controller;


import com.lawnroad.account.dto.ClientSignupRequest;
import com.lawnroad.account.dto.LoginRequest;
import com.lawnroad.account.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private ClientService clientService;

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

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
//        boolean success = clientService.login(request);
//        if (success) {
//            // JWT 또는 다른 인증 토큰 발급이 있다면 여기에 포함
//            Map<String, Object> response = new HashMap<>();
//            response.put("token", "sample-token"); // 실제 구현 시 JWT 사용 가능
//            return ResponseEntity.ok(response);
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("이메일 또는 비밀번호 오류");
//        }
//
//
//    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmailDuplicate(@RequestParam String email) {
        boolean available = clientService.isEmailAvailable(email);

        Map<String, Object> response = new HashMap<>();
        response.put("available", available);

        return ResponseEntity.ok(response);
    }








}
