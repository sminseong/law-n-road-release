package com.lawnroad.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 이달의 수익 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyRevenueDto {
    private Long totalRevenue;           // 총 수익 (환불 차감 후)
    private Long consultationRevenue;    // 상담 수익 (환불 차감 후)
    private Long templateRevenue;        // 템플릿 수익 (환불 차감 후)
    private Long totalRefundAmount;      // 총 환불 금액
    private String revenueMonth;         // 수익 월 (YYYY-MM)
}
