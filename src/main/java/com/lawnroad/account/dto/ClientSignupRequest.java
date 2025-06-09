package com.lawnroad.account.dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClientSignupRequest {

//    private String client_id;
//    private String password;
//    private String email;
//    private String name;
//    private String nickname;
//    private String phone;

    private String clientId;
    private String nickname;
    private String phone;
    private String fullName;
    private String email;
    private String password;
    private String type; // 'CLIENT' 이 들어감

}
