package com.lawnroad.template.controller;

import com.lawnroad.template.dto.*;
import com.lawnroad.template.dto.cart.CheckoutResponseDto;
import com.lawnroad.template.dto.order.ClientOrderListDto;
import com.lawnroad.template.dto.order.ClientOrderListResponseDto;
import com.lawnroad.template.dto.order.ClientOrderTemplateDto;
import com.lawnroad.template.dto.order.ClientOrderTemplateListResponseDto;
import com.lawnroad.template.service.ClientTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/client/templates")
public class ClientTemplateController {
  
  private final ClientTemplateService clientTemplateService;
  
  /**
   * 전체 주문 목록 조회 (필터 및 페이징 포함) (클라이언트 권한)
   */
  @GetMapping("/orders")
  public ResponseEntity<ClientOrderListResponseDto> getOrders(
      @RequestParam(required = false) String status,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit
  ) {
    // TODO: 유저 번호는 하드코딩
    Long userNo = 1L;
    
    // 목록 + 총 개수 조회
    List<ClientOrderListDto> list = clientTemplateService.findOrders(userNo, status, page, limit);
    int totalCount = clientTemplateService.countOrders(userNo, status);
    
    // System.out.println(list);
    
    // 총 페이지 계산
    int totalPages = (int) Math.ceil((double) totalCount / limit);
    
    // 응답 객체 구성
    ClientOrderListResponseDto response = new ClientOrderListResponseDto();
    response.setOrders(list);
    response.setTotalPages(totalPages);
    
    // 다운로드 상태 조회 (order 로만 - 하나라도 다운로드 햇으면 전체 다운로드 인걸로 취급)
    for (ClientOrderListDto order : response.getOrders()) {
      boolean isDownloaded = clientTemplateService.checkIsDownloaded(order.getOrderNo());
      order.setIsDownloaded(isDownloaded ? 1 : 0);
    }
    
    return ResponseEntity.ok(response);
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
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit
  ) {
    // 목록 조회
    List<ClientOrderTemplateDto> list =
        clientTemplateService.findTemplatesByOrder(orderNo, type, categoryNo, isDownloaded);
    
    // 개수 조회
    int totalCount =
        clientTemplateService.countTemplatesByOrder(orderNo, type, categoryNo, isDownloaded);
    
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
  
  
}