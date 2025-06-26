package com.lawnroad.account.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClientDto {
    private Long no;
    private String type;       // 고정값: CLIENT
    private String name;
    private String id;
    private String phone;
    private String email;
    private LocalDateTime createdAt;
    private String status;     // '회원' 또는 '탈퇴'
}
