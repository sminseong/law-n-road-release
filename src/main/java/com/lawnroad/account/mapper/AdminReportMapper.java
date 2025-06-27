package com.lawnroad.account.mapper;


import com.lawnroad.account.dto.BroadcastReportDTO;
import com.lawnroad.account.dto.ChatReportDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface AdminReportMapper {

    List<BroadcastReportDTO> selectUnpenalizedReports();


    void deductLawyerPoint(@Param("broadcastNo") Long broadcastNo);
    void markReportAsPenalized(@Param("broadcastNo") Long broadcastNo);

    List<ChatReportDto> selectPenalizedChatReports();


    void updateClientPenalty(@Param("userNo") Long userNo, @Param("stopDate") LocalDate stopDate);

    int updateReportStatus(@Param("reportNo") Long reportNo);

    void releaseClientStop();

    void applyPenaltyClient_M(@Param("userNo") Long userNo);

    void applyPenaltyClient_M1(@Param("userNo") Long userNo);




}
