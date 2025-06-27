package com.lawnroad.account.service;


import com.lawnroad.account.dto.ClientDto;
import com.lawnroad.account.mapper.ClientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminClientService {

    private final ClientMapper clientMapper;

    public List<ClientDto> getClients(int offset, int limit, String status, String type) {
        return clientMapper.selectClients(offset, limit, status, type);
    }

    public int getClientCount(String status, String keyword) {
        return clientMapper.countClientList(status, keyword);
    }
}
