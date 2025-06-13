package com.lawnroad.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawnroad.board.dto.BoardCreateDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ClientBoardIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Disabled
    void 게시글등록_통합성공() throws Exception {
        BoardCreateDto dto = new BoardCreateDto();
        dto.setTitle("통합 테스트 제목");
        dto.setContent("통합 테스트 내용 100자 이상입니다. 충분히 길고 길고 길게 길게 길게 작성합니다...");
        dto.setIncidentDate(LocalDate.now());
        dto.setUserNo(1L);
        dto.setCategoryNo(3L);

        mockMvc.perform(post("/api/client/qna")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }
}