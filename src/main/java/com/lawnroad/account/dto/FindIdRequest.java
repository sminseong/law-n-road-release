package com.lawnroad.account.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindIdRequest {

    private String fullName;
    private String email;

}
