package com.lawnroad.account.dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LawyerSignupRequest {

    private String lawyerId;
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private String officeName;
    private String zipCode;
    private String landCode;
    private String detailCode;
    private String type; // 'Lawyer' 이 들어감

}
