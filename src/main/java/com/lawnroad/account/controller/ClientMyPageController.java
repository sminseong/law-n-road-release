//package com.lawnroad.account.controller;
//
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping
//public class ClientMyPageController {
//
//    @GetMapping("/mypage")
//    @PreAuthorize("hasRole('CLIENT')")
//    public ResponseEntity<String> clientMypage() {
//        return ResponseEntity.ok("클라이언트 전용 마이페이지입니다.");
//    }
//}
