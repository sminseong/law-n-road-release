package com.lawnroad.admin.service;

import com.lawnroad.admin.dto.AdPurchaseDto;
import com.lawnroad.admin.dto.AdPurchaseListResponseDto;
import com.lawnroad.admin.dto.AdPurchaseSearchConditionDto;
import com.lawnroad.admin.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
  private final AdminMapper mapper;
  
  public AdPurchaseListResponseDto getAdPurchases(AdPurchaseSearchConditionDto cond) {
    List<AdPurchaseDto> items = mapper.selectAdPurchases(cond);
    int totalCount = mapper.countAdPurchases(cond);
    int totalPages = (int) Math.ceil((double) totalCount / cond.getLimit());
    
    AdPurchaseListResponseDto resp = new AdPurchaseListResponseDto();
    resp.setItems(items);
    resp.setTotalCount(totalCount);
    resp.setTotalPages(totalPages);
    return resp;
  }
  
  @Transactional
  public void updateApprovalStatus(Long adNo, String status) {
    mapper.updateApprovalStatus(adNo, status);
  }
}
