package com.lawnroad.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.index.qual.HasSubsequence;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientProfileDTO {

    private String nickname;
    private String email;
    private String phone;

}
