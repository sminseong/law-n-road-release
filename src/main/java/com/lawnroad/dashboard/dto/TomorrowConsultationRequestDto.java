package com.lawnroad.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TomorrowConsultationRequestDto {
    private Long requestId;           // 상담신청 ID
    private String clientName;        // 신청자 이름
    private String clientPhone;       // 신청자 전화번호
    private String requestTime;       // 신청 시간
    private String requestDateTime;   // 신청 일시
}
