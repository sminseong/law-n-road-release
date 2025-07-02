package com.lawnroad.template.controller;

import com.lawnroad.common.util.JwtTokenUtil;
import com.lawnroad.template.dto.*;
import com.lawnroad.template.dto.cart.CheckoutResponseDto;
import com.lawnroad.template.dto.order.ClientOrderListDto;
import com.lawnroad.template.dto.order.ClientOrderListResponseDto;
import com.lawnroad.template.dto.order.ClientOrderTemplateDto;
import com.lawnroad.template.dto.order.ClientOrderTemplateListResponseDto;
import com.lawnroad.template.service.ClientTemplateService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/client/templates")
public class ClientTemplateController {
  
  private final ClientTemplateService clientTemplateService;
  private final JwtTokenUtil jwtUtil;
  
  /**
   * 전체 주문 목록 조회 (필터 및 페이징 포함) (클라이언트 권한)
   */
  @GetMapping("/orders")
  public ResponseEntity<ClientOrderListResponseDto> getOrders(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String status,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit,
      @RequestParam Long no
  ) {
    
    System.out.println("=== 주문 조회 시작 ===");
    System.out.println("no: " + no + ", keyword: " + keyword + ", status: " + status + ", page: " + page + ", limit: " + limit);
    
    try {
      // 1) 페이징된 주문 목록(검색어+상태 필터 적용)
      System.out.println("1. findOrders 호출 시작");
      List<ClientOrderListDto> list = clientTemplateService.findOrders(no, keyword, status, page, limit);
      System.out.println("1. findOrders 완료, 결과 개수: " + (list != null ? list.size() : "null"));
      
      System.out.println("2. countOrders 호출 시작");
      int totalCount = clientTemplateService.countOrders(no, keyword, status);
      System.out.println("2. countOrders 완료, 총 개수: " + totalCount);
      
      // 2) 전체 건수(검색어+상태 필터 적용)
      int totalPages = (int) Math.ceil((double) totalCount / limit);
      System.out.println("3. totalPages 계산 완료: " + totalPages);
      
      // 응답 객체 구성
      ClientOrderListResponseDto response = new ClientOrderListResponseDto();
      response.setOrders(list);
      response.setTotalPages(totalPages);
      
      System.out.println("4. 다운로드 상태 체크 시작");
      // 다운로드 상태 조회 (order 로만 - 하나라도 다운로드 햇으면 전체 다운로드 인걸로 취급)
      for (ClientOrderListDto order : response.getOrders()) {
        System.out.println("다운로드 체크 - orderNo: " + order.getOrderNo());
        boolean isDownloaded = clientTemplateService.checkIsDownloaded(order.getOrderNo());
        order.setIsDownloaded(isDownloaded ? 1 : 0);
      }
      System.out.println("4. 다운로드 상태 체크 완료");
      
      System.out.println("=== 주문 조회 성공 ===");
      return ResponseEntity.ok(response);
      
    } catch (Exception e) {
      System.err.println("=== 주문 조회 에러 ===");
      e.printStackTrace(); // 👈 이게 중요! 정확한 에러 스택트레이스
      return ResponseEntity.status(500).body(null);
    }
  }
  
  /**
   * 특정 주문의 템플릿 상세 목록 list 조회 (필터 포함) (클라이언트 권한)
   */
  @GetMapping("/orders/{orderNo}/items")
  public ResponseEntity<ClientOrderTemplateListResponseDto> getTemplatesByOrder(
      @PathVariable Long orderNo,
      @RequestParam(required = false) String type,
      @RequestParam(required = false) Long categoryNo,
      @RequestParam(required = false) Integer isDownloaded,
      @RequestParam(required = false) String keyword,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit
  ) {
    // 목록 조회
    List<ClientOrderTemplateDto> list =
        clientTemplateService.findTemplatesByOrder(orderNo, type, categoryNo, isDownloaded, keyword);
    
    // 개수 조회
    int totalCount =
        clientTemplateService.countTemplatesByOrder(orderNo, type, categoryNo, isDownloaded, keyword);
    
    // 페이지 수 계산
    int totalPages = (int) Math.ceil((double) totalCount / limit);
    
    // 페이징 잘라서 자르기
    int fromIndex = Math.min((page - 1) * limit, list.size());
    int toIndex = Math.min(fromIndex + limit, list.size());
    List<ClientOrderTemplateDto> paginated = list.subList(fromIndex, toIndex);
    
    // 응답 구성
    ClientOrderTemplateListResponseDto dto = new ClientOrderTemplateListResponseDto();
    dto.setTemplates(paginated);
    dto.setTotalPages(totalPages);
    
    return ResponseEntity.ok(dto);
  }
  
  /**
   * 템플릿 상세 조회 (클라이언트 권한)
   * @param templateNo 템플릿 PK
   * @param type 템플릿 유형 (EDITOR 또는 FILE)
   */
  @GetMapping("/orders/{templateNo}")
  public ResponseEntity<?> getTemplateDetail(
      @PathVariable Long templateNo,
      @RequestParam String type
  ) {
    System.out.println("요청 templateNo=" + templateNo + ", type=" + type);
    if ("EDITOR".equalsIgnoreCase(type)) {
      ClientEditorTemplateDetailDto dto = clientTemplateService.getEditorTemplateDetail(templateNo);
      
      System.out.println("--------------- editor DTO=" + dto);
      return ResponseEntity.ok(dto);
    } else if ("FILE".equalsIgnoreCase(type)) {
      ClientFileTemplateDetailDto dto = clientTemplateService.getFileTemplateDetail(templateNo);
      
      System.out.println("------------------ file DTO=" + dto);
      return ResponseEntity.ok(dto);
    } else {
      return ResponseEntity.badRequest().body("잘못된 템플릿 유형입니다: " + type);
    }
  }
  
  // 다운로드 상태로 변경 (클라이언트 권한)
  @PostMapping("/orders/download")
  public ResponseEntity<Void> markOrderDownloaded(@RequestBody CheckoutResponseDto dto) {
    clientTemplateService.markTemplateAsDownloaded(dto.getOrderNo(), dto.getTmplNo());
    return ResponseEntity.ok().build();
  }
  
  // 다운로드 상태 조회 (개별 조회) (클라이언트 권한)
  @PostMapping("/orders/isDownloaded")
  public ResponseEntity<?> checkDownloaded(@RequestBody CheckoutResponseDto dto) {
    boolean isDownloaded = clientTemplateService.checkIsDownloaded(dto.getOrderNo(), dto.getTmplNo());
    return ResponseEntity.ok(Map.of("isDownloaded", isDownloaded));
  }
  
  // 사용자 마이페이지 -> 최근 5건의 구매내역
  @GetMapping("/orders/recent")
  public ResponseEntity<Map<String, Object>> getRecentOrders(@RequestHeader("Authorization") String authHeader) {
    String token = authHeader.replace("Bearer ", "");
    Claims claims = jwtUtil.parseToken(token);
    long userNo = claims.get("no", Long.class);
    
    List<ClientOrderListDto> orders = clientTemplateService.findRecentOrders(userNo);
    
    Map<String, Object> result = new HashMap<>();
    result.put("orders", orders);
    
    return ResponseEntity.ok(result);
  }
}