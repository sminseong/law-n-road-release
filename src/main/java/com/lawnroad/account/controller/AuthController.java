package com.lawnroad.account.controller;
import com.lawnroad.account.dto.*;
import com.lawnroad.account.entity.ClientEntity;
import com.lawnroad.account.entity.UserEntity;
import com.lawnroad.account.mapper.UserMapper;
import com.lawnroad.account.service.ClientService;
import com.lawnroad.account.service.LawyerService;
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
    private LawyerService lawyerService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserMapper userMapper;
    private final ClientService clientService;

    @GetMapping("/check-id")
    public ResponseEntity<Map<String, Object>> checkIdDuplicate(@RequestParam String clientId) {  // 여기는 클라이언트 아이디를 중복 확인 하는 함수
        boolean available = clientService.isClientIdAvailable(clientId);

        Map<String, Object> response = new HashMap<>();
        response.put("available", available);

        return ResponseEntity.ok(response); // 요청 성공했고, 이 데이터 줄게!  200일 때만 적용됨
    }

    @GetMapping("/lawyer_check-id")
    public ResponseEntity<Map<String, Object>> checkLawyerIdDuplicate(@RequestParam String lawyerId) { // 여기는 변호사 아이디를 중복 확인 하는 함수
        boolean available = lawyerService.isLawyerAvailable(lawyerId);

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

    @PostMapping("/lawyer_signup")
    public ResponseEntity<?> lawyer_signup(@RequestBody LawyerSignupRequest request) {
        lawyerService.registerLawyer(request);
        return ResponseEntity.ok().body("변호사 회원가입 완료");
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

//아이디 찾기
    @PostMapping("/find-id")
    public ResponseEntity<?> findClientId(@RequestBody FindIdRequest request) {
        String clientId = clientService.findClientId(request.getFullName(), request.getEmail());

        if (clientId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("일치하는 아이디를 찾을 수 없습니다.");
        }

        return ResponseEntity.ok(Map.of("clientId", clientId));
    }
//비번찾기
@PostMapping("/reset-password")
public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
    boolean success = clientService.resetPassword(request.getEmail(), request.getNewPassword());

    if (!success) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 이메일로 등록된 계정이 없습니다.");
    }

    return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
}


}
