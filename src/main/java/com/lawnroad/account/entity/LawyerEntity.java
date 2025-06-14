package com.lawnroad.account.entity;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LawyerEntity {


    private Long no;
    private String lawyerId;
    private String pw_hash;
    private String profile;
    private String email;
    private String name;
    private String office_number;
    private String office_name;
    private String phone;
    private String zip_code;
    private String land_address;
    private String detail_address;
    private int point;
    private int content;
    private String status;
    private int consult_price;

}

