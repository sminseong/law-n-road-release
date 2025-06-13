package com.lawnroad.account.controller;


import com.lawnroad.account.dto.ClientSignupRequest;
import com.lawnroad.account.dto.LoginRequest;
import com.lawnroad.account.dto.LoginResponseDto;
import com.lawnroad.account.entity.ClientEntity;
import com.lawnroad.account.entity.UserEntity;
import com.lawnroad.account.mapper.UserMapper;
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
    private final UserMapper userMapper;

    @GetMapping("/check-id")
    public ResponseEntity<Map<String, Object>> checkIdDuplicate(@RequestParam String clientId) {
        boolean available = clientService.isClientIdAvailable(clientId);

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
        try {
            ClientEntity client = clientService.login(request.getClientId(), request.getPassword());

            //ClientEntity client = clientMapper.findByClientId(clientId);
            UserEntity user = userMapper.findByNo(client.getNo());
            String accessToken = jwtTokenUtil.generateAccessToken(client.getClientId(),client.getNo(),user.getType(),client.getNickname());
            String refreshToken = jwtTokenUtil.generateRefreshToken(client.getClientId());

            // 🔍 여기에서 확인
            System.out.println("✅ Access Token: " + accessToken);
            //jwtTokenUtil.printPayload(accessToken); // 👈 payload 출력
            Long no = jwtTokenUtil.getUserNoFromToken(accessToken);
            String nickname = jwtTokenUtil.getNicknameFromToken(accessToken);
            String role = jwtTokenUtil.getRoleFromToken(accessToken);
            System.out.println("test 진행 중 : " + no);
            System.out.println("test 진행 중 : " + nickname);
            System.out.println("test 진행 중 : " + role);

            jwtTokenUtil.storeRefreshToken(client.getClientId(), refreshToken);

            Map<String, Object> result = new HashMap<>();
            result.put("accessToken", accessToken);
            result.put("refreshToken", refreshToken);
            result.put("name", client.getName());
            result.put("nickname", client.getNickname());
            result.put("role", user.getType());

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
        }
    }





}
