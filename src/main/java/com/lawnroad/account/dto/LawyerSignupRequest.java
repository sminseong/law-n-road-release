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
    private String officeNumber;
    private String zipcode;
    private String roadAddress;
    private String landAddress;
    private String detailAddress;
    private int consent; // 0 or 1
    private String type; // "LAWYER"
//추가 필드
    private String lawyerIntro;
    private String introDetail;

}
