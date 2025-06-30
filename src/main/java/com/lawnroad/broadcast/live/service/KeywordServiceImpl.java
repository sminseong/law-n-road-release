package com.lawnroad.broadcast.live.service;

import com.lawnroad.broadcast.live.dto.KeywordAlertMatchDto;
import com.lawnroad.broadcast.live.mapper.KeywordMapper;
import com.lawnroad.broadcast.live.model.KeywordVo;
import com.lawnroad.notification.dto.BroadcastCreatedDto;
import com.lawnroad.notification.dto.BroadcastStartedDto;
import com.lawnroad.notification.service.BroadcastCreatedService;
import com.lawnroad.notification.service.BroadcastStartedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class KeywordServiceImpl implements KeywordService {

    private final KeywordMapper keywordMapper;
    private final BroadcastStartedService broadcastStartedService;
    private final BroadcastCreatedService broadcastCreatedService;

    @Override
    public void insertKeyword(KeywordVo keywordVo) {
        keywordMapper.insertKeyword(keywordVo);
    }

    @Override
    public void registerKeywordAlert(Long userNo, String keyword) {
        boolean exists = keywordMapper.existsByUserNoAndKeyword(userNo, keyword);
        if (exists) {
            throw new IllegalArgumentException("이미 완료된 알림신청입니다.");
        }

        int result = keywordMapper.insertKeywordAlert(userNo, keyword);
        if (result == 0) {
            throw new RuntimeException("알림 신청에 실패했습니다.");
        }
    }

    @Override
    public void notifyKeywordMatchedUsers(Long scheduleNo, String broadcastTitle) {
        // 1. 방송 키워드 가져오기
        List<String> scheduleKeywords = keywordMapper.findKeywordsByScheduleNo(scheduleNo);

        // 2. 방송자 이름 가져오기
        String lawyerName = keywordMapper.findLawyerNameByScheduleNo(scheduleNo);

        // 3. 키워드 리스트 구성
        List<String> alertKeywords = new ArrayList<>(scheduleKeywords);
        alertKeywords.add(lawyerName);  // 이름도 키워드처럼 포함

        // 4. 키워드 알림 신청 유저 조회
        List<KeywordAlertMatchDto> matchedUsers = keywordMapper.findUsersByKeywords(alertKeywords);

        // 5. 알림톡 발송
        Set<Long> alreadySentUserNos = new HashSet<>();

        for (KeywordAlertMatchDto user : matchedUsers) {
            if (alreadySentUserNos.contains(user.getUserNo())) {
                continue;  // 이미 보낸 사용자면 skip
            }

            BroadcastStartedDto dto = new BroadcastStartedDto();
            dto.setTo(user.getPhone());
            dto.setName(user.getName());
            dto.setTitle(broadcastTitle);
            dto.setStart(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));

            broadcastStartedService.send(dto);

            alreadySentUserNos.add(user.getUserNo());  // 발송 완료된 사용자 기록
        }
    }

    @Override
    public void notifyKeywordMatchedUsersForSchedule(Long scheduleNo, String broadcastTitle, String startTime, String lawyerName) {
        // 1. 키워드 가져오기
        List<String> scheduleKeywords = keywordMapper.findKeywordsByScheduleNo(scheduleNo);

        // 2. 키워드 목록 구성
        List<String> alertKeywords = new ArrayList<>(scheduleKeywords);
        alertKeywords.add(lawyerName);  // 이름도 키워드로 포함

        // 3. 알림 신청한 사용자 조회
        List<KeywordAlertMatchDto> matchedUsers = keywordMapper.findUsersByKeywords(alertKeywords);

        // 4. 중복 제거 및 알림톡 발송
        Set<Long> alreadySentUserNos = new HashSet<>();
        for (KeywordAlertMatchDto user : matchedUsers) {
            if (alreadySentUserNos.contains(user.getUserNo())) continue;

            BroadcastCreatedDto dto = new BroadcastCreatedDto();
            dto.setTo(user.getPhone());
            dto.setName(user.getName());
            dto.setLawyer(lawyerName);
            dto.setTitle(broadcastTitle);
            dto.setStart(startTime);

            broadcastCreatedService.send(dto);

            alreadySentUserNos.add(user.getUserNo());
        }
    }

}
