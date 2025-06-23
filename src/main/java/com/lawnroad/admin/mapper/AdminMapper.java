package com.lawnroad.admin.mapper;

import com.lawnroad.admin.dto.AdPurchaseDto;
import com.lawnroad.admin.dto.AdPurchaseSearchConditionDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminMapper {
  // 광고 관리 조회 등
  List<AdPurchaseDto> selectAdPurchases(AdPurchaseSearchConditionDto cond);
  int                 countAdPurchases(AdPurchaseSearchConditionDto cond);
  
  // 광고 승인 반려 관련
  void updateApprovalStatus(
      @Param("no") Long adNo,
      @Param("status") String approvalStatus
  );
  
  // 회원 관리 조회 등
  
  // 예약 관리 조회 등
  
  // 패널티 관리 조회 등
}
