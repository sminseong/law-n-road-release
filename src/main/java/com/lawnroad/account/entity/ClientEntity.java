package com.lawnroad.account.entity;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientEntity {

    private Long no;
    private String client_id;
    private String pw_hash;
    private String email;
    private String name;
    private String nickname;
    private String phone;
    private int content;
    private int alert_content;

}
