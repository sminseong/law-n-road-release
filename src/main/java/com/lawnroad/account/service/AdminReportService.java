package com.lawnroad.account.service;

import com.lawnroad.account.dto.BroadcastReportDTO;
import com.lawnroad.account.dto.ChatReportDto;
import com.lawnroad.account.mapper.AdminReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminReportService {

    private final AdminReportMapper adminReportMapper;

    public List<BroadcastReportDTO> getUnpenalizedReports() {
        System.out.println("getUnpenalizedReports");
        return adminReportMapper.selectUnpenalizedReports();
    }


    @Transactional
    public void applyPenalty(Long broadcastNo) {
        // 방송번호로 변호사 포인트 차감
        adminReportMapper.deductLawyerPoint(broadcastNo);

        // 해당 신고건 status = 1로 처리
        adminReportMapper.markReportAsPenalized(broadcastNo);
    }


    public List<ChatReportDto> getPenalizedChatReports() {
        System.out.println("getPenalizedChatReports");
        return adminReportMapper.selectPenalizedChatReports();
    }

    public void penalizeClientByUserNo(Long userNo) {
        System.out.println("의뢰인 패널티 부여");
        LocalDate tomorrow = LocalDate.now();
        adminReportMapper.updateClientPenalty(userNo, tomorrow);
    }


    public void penalizeReport(Long reportNo) {
        int updated = adminReportMapper.updateReportStatus(reportNo);
        if (updated == 0) {
            throw new IllegalArgumentException("해당 신고번호의 신고가 존재하지 않거나 이미 처리되었습니다.");
        }
    }





}
