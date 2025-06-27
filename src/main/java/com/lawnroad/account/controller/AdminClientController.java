package com.lawnroad.account.controller;


import com.lawnroad.account.dto.ClientDto;
import com.lawnroad.account.service.AdminClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/client")
@RequiredArgsConstructor
public class AdminClientController {

    private final AdminClientService adminClientService;

    @GetMapping
    public ResponseEntity<?> getClients(
            @RequestParam int offset,
            @RequestParam int limit,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type
    ) {
        List<ClientDto> list = adminClientService.getClients(offset, limit, status, type);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        return ResponseEntity.ok(result);
    }
}
