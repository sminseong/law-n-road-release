package com.lawnroad.account.mapper;


import com.lawnroad.account.dto.BroadcastReportConfirmDTO;
import com.lawnroad.account.dto.ChatReportConfirmDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AdminReportMapper {

    List<BroadcastReportConfirmDTO> selectUnpenalizedReports();


    void deductLawyerPoint(@Param("broadcastNo") Long broadcastNo);
    void markReportAsPenalized(@Param("broadcastNo") Long broadcastNo);

    List<ChatReportConfirmDto> selectPenalizedChatReports();


    void updateClientPenalty(@Param("userNo") Long userNo, @Param("stopDate") LocalDate stopDate);

    int updateReportStatus(@Param("reportNo") Long reportNo);

    void releaseClientStop();

    void applyPenaltyClient_M(@Param("userNo") Long userNo);

    void applyPenaltyClient_M1(@Param("userNo") Long userNo);




}
