package com.lawnroad.account.service;


import com.lawnroad.account.dto.ClientSignupRequest;
import com.lawnroad.account.dto.LoginRequest;
import com.lawnroad.account.entity.ClientEntity;
import com.lawnroad.account.entity.UserEntity;
import com.lawnroad.account.mapper.ClientMapper;

import com.lawnroad.account.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientService {

    @Autowired
    private ClientMapper clientMapper;
    private UserMapper userMapper;
    private PasswordEncoder passwordEncoder;

    public ClientService(UserMapper userMapper, ClientMapper clientMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.clientMapper = clientMapper;
        this.passwordEncoder = passwordEncoder;


    }



    public boolean isClientIdAvailable(String clientId) {
        int count = clientMapper.countByClientId(clientId);
        return count == 0; // 0 이면 사용 가능, true 반환
    }

    public boolean isClientNickNameAvailable(String nickname) {
        int count = clientMapper.countByClientNickName(nickname);
        return count == 0; // 0 이면 사용 가능, true 반환
    }


    @Transactional
    public void registerClient(ClientSignupRequest request) {
        // 1. user 테이블 삽입
        UserEntity user = new UserEntity();
        user.setType("CLIENT");
        userMapper.insertUser(user); // user.no가 자동 생성됨

        // 2. client 테이블 삽입
        ClientEntity client = new ClientEntity();
        client.setNo(user.getNo());
        client.setClient_id(request.getClientId());
        client.setPw_hash(passwordEncoder.encode(request.getPassword()));
        client.setEmail(request.getEmail());
        client.setName(request.getFullName());
        client.setNickname(request.getNickname());
        client.setPhone(request.getPhone());
        client.setContent(1);
        client.setAlert_content(1);
        clientMapper.insertClient(client);
    }


    // ClientService.java
//    public boolean login(LoginRequest request) {
//        ClientEntity client = clientMapper.findByEmail(request.getEmail());
//        if (client == null) return false;
//
//        // 비밀번호 비교
//        return passwordEncoder.matches(request.getPassword(), client.getPw_hash());
//    }



    public boolean isEmailAvailable(String email) {
        int count = clientMapper.countByEmail(email);
        return count == 0; // 0이면 사용 가능
    }










}
