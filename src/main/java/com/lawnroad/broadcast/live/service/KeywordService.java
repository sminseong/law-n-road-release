package com.lawnroad.broadcast.live.service;

import com.lawnroad.broadcast.live.model.KeywordVo;

public interface KeywordService {
    void insertKeyword(KeywordVo keywordVo);
    void registerKeywordAlert(Long userNo, String keyword);

    //방송 알림
    void notifyKeywordMatchedUsers(Long scheduleNo, String broadcastTitle);
    void notifyKeywordMatchedUsersForSchedule(Long scheduleNo, String broadcastTitle, String startTime, String lawyerName);
}
