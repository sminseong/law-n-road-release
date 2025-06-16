package com.lawnroad.common.util;

import com.lawnroad.account.entity.ClientEntity;
import com.lawnroad.account.mapper.UserMapper;
import com.lawnroad.account.service.ClientService;
import com.lawnroad.account.service.LawyerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserContext {

    private final JwtTokenUtil jwtTokenUtil;

    public Long getUserNo(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtTokenUtil.getUserNoFromToken(token);
    }

    public String getNickname(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtTokenUtil.getNicknameFromToken(token);
    }

}
