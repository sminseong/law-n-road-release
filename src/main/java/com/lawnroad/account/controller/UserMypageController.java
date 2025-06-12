//package com.lawnroad.account.controller;
//
//
//import com.lawnroad.common.util.JwtTokenUtil;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/user")
//public class UserMypageController {
//
////    private final JwtTokenUtil jwtTokenUtil;
////
////    public UserMypageController(JwtTokenUtil jwtTokenUtil) {
////        this.jwtTokenUtil = jwtTokenUtil;
////    }
////
////    @GetMapping("/mypage-info")
////    public ResponseEntity<?> mypageInfo(@RequestHeader("Authorization") String authHeader) {
////        String token = authHeader.replace("Bearer ", "");
////
////        Long userNo = jwtTokenUtil.getUserNoFromToken(token);
////        String nickname = jwtTokenUtil.getNicknameFromToken(token);
////
////        // 💡 여기가 진짜로 프린트만 하는 부분!
////        System.out.println("🔥 [MYPAGE] userNo: " + userNo);
////        System.out.println("🔥 [MYPAGE] nickname: " + nickname);
////
////        // 응답은 편의상 내려주지만, 프린트로만 확인해도 됨
////        Map<String, Object> result = new HashMap<>();
////        result.put("userNo", userNo);
////        result.put("nickname", nickname);
////        return ResponseEntity.ok(result);
////    }
//
//
//
//
//}
