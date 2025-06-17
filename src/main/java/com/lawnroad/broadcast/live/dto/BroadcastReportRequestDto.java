package com.lawnroad.broadcast.live.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BroadcastReportRequestDto {
    private Long broadcastNo;       // 신고 대상 방송 번호
    private Long userNo;            // 신고자 유저 번호
    private String reasonCode;      // 신고 사유 코드
    private String detailReason;    // 상세 신고 내용 (선택)
}
