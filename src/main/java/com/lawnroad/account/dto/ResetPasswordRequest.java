package com.lawnroad.account.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {

    private String email;
    private String newPassword;
    private String userId;        // 아이디
    private String fullName;      // 사용자 이름
    private String userType;      // client 또는 lawyer




}
