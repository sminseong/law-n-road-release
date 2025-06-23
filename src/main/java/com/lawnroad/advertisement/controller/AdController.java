package com.lawnroad.advertisement.controller;

import com.lawnroad.advertisement.dto.AdRegisterDto;
import com.lawnroad.advertisement.dto.AdvertisementDetailResponseDto;
import com.lawnroad.advertisement.dto.PagedAdvertisementResponseDto;
import com.lawnroad.advertisement.service.AdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lawyer/ads")
public class AdController {
  
  private final AdService advertisementService;
  
  // 전체 목록 조회
  @GetMapping
  public ResponseEntity<PagedAdvertisementResponseDto> getMyAds(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit) {
    
    Long userNo = 1L; // TODO: 하드코딩
    
    PagedAdvertisementResponseDto result = advertisementService.getAdsByUserWithPaging(userNo, page, limit);
    
    return ResponseEntity.ok(result);
  }
  
  // 상세 조회
  @GetMapping("/{adNo}")
  public ResponseEntity<?> getAdDetail(@PathVariable Long adNo) {
    Long userNo = 1L; // TODO: 하드코딩
    System.out.println("adNo: " + adNo);
    AdvertisementDetailResponseDto ad = advertisementService.getAdDetailByAdNo(adNo);
    if (!ad.getUserNo().equals(userNo)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인 광고만 조회할 수 있습니다.");
    }
    return ResponseEntity.ok(ad);
  }
  
  // 광고 등록
  @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> registerAd(@ModelAttribute AdRegisterDto dto) {
    try {
      // 임시 하드코딩 (TODO: 추후 로그인 연동)
      dto.setUserNo(1L);
      
      advertisementService.createAdOrderAndRegister(dto);
      return ResponseEntity.ok("✅ 광고가 성공적으로 등록되었습니다.");
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return ResponseEntity.internalServerError().body("❌ 광고 등록 실패");
    }
  }
  
  
  // 광고 삭제
  @DeleteMapping("/{adNo}")
  public ResponseEntity<?> deleteAd(@PathVariable Long adNo) {
    Long userNo = 1L; // TODO: 하드코딩
    
    try {
      // 권한 체크
      AdvertisementDetailResponseDto ad = advertisementService.getAdDetailByAdNo(adNo);
      if (!ad.getUserNo().equals(userNo)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인 광고만 삭제할 수 있습니다.");
      }
      
      // 삭제 실행
      advertisementService.deleteAd(adNo);
      
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("삭제 중 오류가 발생했습니다: " + e.getMessage());
    }
  }
}