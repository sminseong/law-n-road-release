package com.lawnroad.broadcast.live.service;

import com.lawnroad.broadcast.live.dto.*;
import com.lawnroad.broadcast.live.mapper.KeywordMapper;
import com.lawnroad.broadcast.live.mapper.ScheduleMapper;
import com.lawnroad.broadcast.live.model.KeywordVo;
import com.lawnroad.broadcast.live.model.ScheduleVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleMapper scheduleMapper;
    private final KeywordService keywordService;
    private final KeywordMapper keywordMapper;

    @Override
    public void registerSchedule(ScheduleRequestDto scheduleRequestDto) {
        ScheduleVo scheduleVo = ScheduleVo.builder()
                .userNo(scheduleRequestDto.getUserNo())
                .categoryNo(scheduleRequestDto.getCategoryNo())
                .name(scheduleRequestDto.getName())
                .content(scheduleRequestDto.getContent())
                .thumbnailPath(scheduleRequestDto.getThumbnailPath())
                .date(scheduleRequestDto.getDate())
                .startTime(scheduleRequestDto.getStartTime())
                .endTime(scheduleRequestDto.getEndTime())
                .build();

        scheduleMapper.insertSchedule(scheduleVo);

        // 방금 insert 된 scheduleNo 가져오기
        Long scheduleNo = scheduleVo.getNo();

        // 키워드 저장
        if (scheduleRequestDto.getKeywords() != null) {
            for (String keyword : scheduleRequestDto.getKeywords()) {
                KeywordVo keywordVo = KeywordVo.builder()
                        .scheduleNo(scheduleNo)
                        .keyword(keyword)
                        .build();
                keywordService.insertKeyword(keywordVo);
            }
        }
    }
    // 클라이언트 방송 스케줄 달력
    @Override
    public List<ScheduleCalendarDto> getSchedulesByMonth(String month) {
        return scheduleMapper.findAllByMonth(month);
    }
    // 클라이언트 방송 스케줄 일별 리스트
    @Override
    public List<ScheduleDateDto> getSchedulesByDate(LocalDate date) {
        return scheduleMapper.findAllByDate(date);
    }
    // 변호사 대시보드 본인 방송 스케줄 리스트 출력용
    public List<ScheduleResponseDto> getSchedulesByLawyer(Long userNo) {
        return scheduleMapper.findAllByLawyer(userNo);
    }

    @Override
    public ScheduleDetailDto findDetailByScheduleNo(Long scheduleNo) {
        return scheduleMapper.findByScheduleNo(scheduleNo);
    }

    @Override
    public void updateSchedule(ScheduleUpdateDto scheduleUpdateDto) {
        scheduleMapper.updateSchedule(scheduleUpdateDto);

        Long scheduleNo = scheduleUpdateDto.getScheduleNo();
        keywordMapper.deleteByScheduleNo(scheduleNo);

        if (scheduleUpdateDto.getKeywords() != null) {
            for (String keyword : scheduleUpdateDto.getKeywords()) {
                KeywordVo keywordVo = KeywordVo.builder()
                        .scheduleNo(scheduleNo)
                        .keyword(keyword)
                        .build();
                keywordMapper.insertKeyword(keywordVo);
            }
        }
    }
}
