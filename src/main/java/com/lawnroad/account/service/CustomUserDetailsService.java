package com.lawnroad.account.service;


import com.lawnroad.account.entity.ClientEntity;
import com.lawnroad.account.mapper.ClientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {


    private final ClientMapper clientMapper;

    @Override
    public UserDetails loadUserByUsername(String client_id) throws UsernameNotFoundException {
        ClientEntity client = clientMapper.findByEmail(client_id);
        if (client == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + client_id);
        }

        return User.builder()
                .username(client.getEmail())
                .password(client.getPwHash()) // 꼭 필요하지는 않지만 UserDetails 구조상 필요
                .roles("USER") // 임시 ROLE 설정
                .build();
    }

}
