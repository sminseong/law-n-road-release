package com.lawnroad.account.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AdminLoginRequest {

    private String adminId;
    private String password;

}
