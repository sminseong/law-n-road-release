package com.lawnroad.broadcast.live.service;

import com.lawnroad.broadcast.live.dto.ScheduleCalendarDto;
import com.lawnroad.broadcast.live.dto.ScheduleDateDto;
import com.lawnroad.broadcast.live.dto.ScheduleRequestDto;
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

    @Override
    public List<ScheduleDateDto> getSchedulesByDate(LocalDate date) {
        return scheduleMapper.findAllByDate(date);
    }

    @Override
    public List<ScheduleCalendarDto> getSchedulesByMonth(String month) {
        return scheduleMapper.findAllByMonth(month);
    }
}
