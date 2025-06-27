package com.lawnroad.account.dto;


import lombok.Data;

@Data
public class LawyerDTO {

    private Long no;
    private String name;
    private String lawyerId;
    private String phone;
    private String email;
    private String createdAt;
    private String status;
    private String profile;
    private String cardFront;
    private String cardBack;

}
