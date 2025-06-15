package com.lawnroad.account.entity;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LawyerEntity {


    private Long no;
    private String lawyerId;
    private String pwHash;
    private String profile;
    private String email;
    private String name;
    private String officeNumber;
    private String phone;
    private String zipcode;
    private String roadAddress;
    private String landAddress;
    private String detailAddress;
    private int point;
    private int consent;
    private String status;
    private int consultPrice;
    private String cardFront;
    private String cardBack;
    private String officeName;
}

