package com.lawnroad.account.controller;


import com.lawnroad.account.dto.LawyerDTO;
import com.lawnroad.account.service.AdminLawyerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/lawyer")
@RequiredArgsConstructor
public class AdminLawyerController {

    private final AdminLawyerService adminLawyerService;

    @GetMapping
    public ResponseEntity<?> getLawyers(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword
    ) {
        Map<String, Object> param = new HashMap<>();
        param.put("offset", offset);
        param.put("limit", limit);
        if (status != null) param.put("status", status);
        if (keyword != null) param.put("keyword", keyword);

        List<LawyerDTO> list = adminLawyerService.getLawyerList(param);
        return ResponseEntity.ok(Collections.singletonMap("list", list));
    }



}
