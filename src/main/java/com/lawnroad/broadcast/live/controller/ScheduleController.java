package com.lawnroad.broadcast.live.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.lawnroad.broadcast.live.dto.*;
import com.lawnroad.broadcast.live.service.ScheduleService;
import com.lawnroad.common.util.FileStorageUtil;
import com.lawnroad.common.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final JwtTokenUtil jwtTokenUtil;
    private final ScheduleService scheduleService;
    private final FileStorageUtil fileStorageUtil;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> registerSchedule(
            @RequestHeader("Authorization") String authHeader,
            //@RequestParam("userNo") Long userNo,
            @RequestParam("categoryNo") Long categoryNo,
            @RequestParam("name") String name,
            @RequestParam("content") String content,
            @RequestParam("date") String date,
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime,
            @RequestParam("thumbnail") MultipartFile thumbnail,
            @RequestParam(value = "keywords", required = false) String keywordsJson
    ) {
        String path = fileStorageUtil.save(thumbnail, "uploads/images", null);
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("썸네일 파일 경로가 없습니다.");
        }

        // keywords JSON을 List<String>으로 변환
        List<String> keywords = null;
        if (keywordsJson != null && !keywordsJson.isBlank()) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                keywords = objectMapper.readValue(keywordsJson, new TypeReference<List<String>>() {
                });
            } catch (Exception e) {
                throw new RuntimeException("키워드 파싱 오류", e);
            }
        }
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtTokenUtil.parseToken(token);
        Long userNo = claims.get("no", Long.class);

        ScheduleRequestDto scheduleRequestDto = ScheduleRequestDto.builder()
                .userNo(userNo)
                .categoryNo(categoryNo)
                .name(name)
                .content(content)
                .thumbnailPath("http://localhost:8080" + path)
                .date(LocalDate.parse(date))
                .startTime(LocalDateTime.parse(startTime))
                .endTime(LocalDateTime.parse(endTime))
                .keywords(keywords)
                .build();

        scheduleService.registerSchedule(scheduleRequestDto);
        return ResponseEntity.ok("방송 스케줄이 성공적으로 등록되었습니다.");
    }

    @GetMapping("/month")
    public ResponseEntity<List<ScheduleCalendarDto>> getMonthlySchedule(@RequestParam String month) {
        List<ScheduleCalendarDto> result = scheduleService.getSchedulesByMonth(month);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{date}")
    public ResponseEntity<List<ScheduleDateDto>> getScheduleByDate(@PathVariable String date) {
        LocalDate parsedDate = LocalDate.parse(date);
        List<ScheduleDateDto> schedules = scheduleService.getSchedulesByDate(parsedDate);
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/my")
    public ResponseEntity<List<ScheduleResponseDto>> getLawyerSchedules(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtTokenUtil.parseToken(token);

        Long userNo = claims.get("no", Long.class);
        List<ScheduleResponseDto> list = scheduleService.getSchedulesByLawyer(userNo);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/my/{scheduleNo}")
    public ResponseEntity<ScheduleDetailDto> getScheduleDetail(@PathVariable Long scheduleNo) {
        ScheduleDetailDto detail = scheduleService.findDetailByScheduleNo(scheduleNo);

        if (detail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(detail);
    }

    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateSchedule(
            @RequestParam("scheduleNo") Long scheduleNo,
            @RequestParam("categoryNo") Long categoryNo,
            @RequestParam("name") String name,
            @RequestParam("content") String content,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestParam(value = "keywords", required = false) String keywordsJson
    ) {
        String path = null;
        if (thumbnail != null && !thumbnail.isEmpty()) {
            path = "http://localhost:8080" + fileStorageUtil.save(thumbnail, "uploads/images", null);
        } else {
            path = scheduleService.findDetailByScheduleNo(scheduleNo).getThumbnailPath();
        }

        // keywords JSON → List<String>
        List<String> keywords = null;
        if (keywordsJson != null && !keywordsJson.isBlank()) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                keywords = objectMapper.readValue(keywordsJson, new TypeReference<List<String>>() {});
            } catch (Exception e) {
                throw new RuntimeException("키워드 파싱 오류", e);
            }
        }

        ScheduleUpdateDto dto = ScheduleUpdateDto.builder()
                .scheduleNo(scheduleNo)
                .categoryNo(categoryNo)
                .name(name)
                .content(content)
                .thumbnailPath(path)
                .keywords(keywords)
                .build();

        scheduleService.updateSchedule(dto);
        return ResponseEntity.ok("스케줄이 성공적으로 수정되었습니다.");
    }

    @DeleteMapping("/delete/{scheduleNo}")
    public ResponseEntity<String> deleteSchedule(@PathVariable Long scheduleNo) {
        int deletedCount = scheduleService.deleteScheduleByNo(scheduleNo);
        if (deletedCount == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("삭제할 스케줄이 존재하지 않습니다.");
        }
        return ResponseEntity.ok("삭제 완료");
    }
}
