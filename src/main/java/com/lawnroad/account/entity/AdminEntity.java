package com.lawnroad.account.entity;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class AdminEntity {

    private Long no;
    private String adminId;
    private String pwHash;
    private String name;
    private String phone;
    private String email;
    private LocalDateTime createdAt;
}
