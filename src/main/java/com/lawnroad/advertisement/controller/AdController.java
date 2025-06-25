package com.lawnroad.advertisement.controller;

import com.lawnroad.advertisement.dto.AdRegisterDto;
import com.lawnroad.advertisement.dto.AdvertisementDetailResponseDto;
import com.lawnroad.advertisement.dto.PagedAdvertisementResponseDto;
import com.lawnroad.advertisement.service.AdService;
import com.lawnroad.common.util.JwtTokenUtil;
import com.lawnroad.payment.model.OrdersVO;
import com.lawnroad.payment.service.OrdersService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lawyer/ads")
public class AdController {

  private final AdService advertisementService;
  private final OrdersService ordersService;
  private final JwtTokenUtil jwtUtil;

  // 전체 목록 조회
  @GetMapping
  public ResponseEntity<PagedAdvertisementResponseDto> getMyAds(
      @RequestHeader("Authorization") String authHeader,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit) {
    
    String token = authHeader.replace("Bearer ", "");
    Claims claims = jwtUtil.parseToken(token);
    long userNo = claims.get("no", Long.class);

    PagedAdvertisementResponseDto result = advertisementService.getAdsByUserWithPaging(userNo, page, limit);

    return ResponseEntity.ok(result);
  }

  // 상세 조회
  @GetMapping("/{adNo}")
  public ResponseEntity<?> getAdDetail(@RequestHeader("Authorization") String authHeader, @PathVariable Long adNo) {
    String token = authHeader.replace("Bearer ", "");
    Claims claims = jwtUtil.parseToken(token);
    long userNo = claims.get("no", Long.class);
    
    System.out.println("adNo: " + adNo);
    AdvertisementDetailResponseDto ad = advertisementService.getAdDetailByAdNo(adNo);
    if (!ad.getUserNo().equals(userNo)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인 광고만 조회할 수 있습니다.");
    }
    return ResponseEntity.ok(ad);
  }

  // 광고 등록 (예전 코드)
  @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> registerAd(@RequestHeader("Authorization") String authHeader, @ModelAttribute AdRegisterDto dto) {
    try {
      String token = authHeader.replace("Bearer ", "");
      Claims claims = jwtUtil.parseToken(token);
      long userNo = claims.get("no", Long.class);
      
      dto.setUserNo(userNo);

      advertisementService.createAdOrderAndRegister(dto);
      return ResponseEntity.ok("✅ 광고가 성공적으로 등록되었습니다.");
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return ResponseEntity.internalServerError().body("❌ 광고 등록 실패");
    }
  }
  // 광고 등록 (결제용 코드)
  @PostMapping(value = "/register-with-order", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> registerAdWithOrder(@RequestHeader("Authorization") String authHeader, @ModelAttribute AdRegisterDto dto) {
      try {
          String token = authHeader.replace("Bearer ", "");
          Claims claims = jwtUtil.parseToken(token);
          long userNo = claims.get("no", Long.class);
          
          dto.setUserNo(userNo);
          
          Long orderNo = advertisementService.createAdOrderAndRegister(dto);
          System.out.println("▶ createAdOrderAndRegister returned orderNo: " + orderNo);
          if (orderNo == null) {
              return ResponseEntity
                      .status(HttpStatus.INTERNAL_SERVER_ERROR)
                      .body(Map.of("message","주문 생성에 실패했습니다."));
          }

          OrdersVO order = ordersService.getOrder(orderNo);
          System.out.println("▶ ordersService.getOrder returned: " + order);

          if (order != null) {
              System.out.println("▶ orderCode: " + order.getOrderCode());
              System.out.println("▶ amount:    " + order.getAmount());
          }

          if (order == null) {
              return ResponseEntity
                      .status(HttpStatus.NOT_FOUND)
                      .body(Map.of("message","생성된 주문을 찾을 수 없습니다."));
          }
          String orderCode = order.getOrderCode();
          Long   amount    = order.getAmount();
          if (orderCode == null || amount == null) {
              return ResponseEntity
                      .status(HttpStatus.INTERNAL_SERVER_ERROR)
                      .body(Map.of("message","주문 정보가 완전하지 않습니다."));
          }

          // 이제 null이 아님을 보장하고 Map.of 사용
          Map<String,Object> body = Map.of(
                  "orderCode", orderCode,
                  "amount",    amount
          );
          return ResponseEntity.ok(body);

      } catch (Exception e) {
          return ResponseEntity
                  .status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body(Map.of("message","광고 등록 실패", "error", e.getMessage()));
      }
  }
  
  // 광고 삭제
  @DeleteMapping("/{adNo}")
  public ResponseEntity<?> deleteAd(@RequestHeader("Authorization") String authHeader, @PathVariable Long adNo) {
    String token = authHeader.replace("Bearer ", "");
    Claims claims = jwtUtil.parseToken(token);
    long userNo = claims.get("no", Long.class);

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