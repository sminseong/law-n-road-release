package com.lawnroad.admin.controller;

import com.lawnroad.admin.dto.AdPurchaseListResponseDto;
import com.lawnroad.admin.dto.AdPurchaseSearchConditionDto;
import com.lawnroad.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/ad-purchases")
@RequiredArgsConstructor
public class AdminController {
  private final AdminService service;
  
  @GetMapping
  public AdPurchaseListResponseDto list(AdPurchaseSearchConditionDto cond) {
    return service.getAdPurchases(cond);
  }
  
  @PostMapping("/{adNo}/approve")
  public ResponseEntity<Void> approveAd(@PathVariable("adNo") Long adNo) {
    service.updateApprovalStatus(adNo, "APPROVED");
    return ResponseEntity.ok().build();
  }
  
  @PostMapping("/{adNo}/reject")
  public ResponseEntity<Void> rejectAd(@PathVariable("adNo") Long adNo) {
    service.updateApprovalStatus(adNo, "REJECTED");
    return ResponseEntity.ok().build();
  }
}
