package com.lawnroad.template.controller;

import com.lawnroad.template.dto.cart.*;
import com.lawnroad.template.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/client/cart")
public class CartController {
  
  private final CartService cartService;
  
  // 1. 장바구니 추가 (클라이언트 권한)
  @PostMapping
  public ResponseEntity<Void> addToCart(@RequestBody CartAddRequestDto dto) {
    Long userNo = 1L; // TODO: @AuthenticationPrincipal 로 교체 예정
    
    boolean added = cartService.addToCart(userNo, dto.getTmplNo());
    if (added) {
      return ResponseEntity.status(201).build(); // Created
    } else {
      return ResponseEntity.status(409).build(); // Conflict (이미 있음)
    }
  }
  
  // 2. 장바구니 삭제 (cart row PK 기준) (클라이언트 권한)
  @DeleteMapping("/{no}")
  public ResponseEntity<Void> removeFromCart(@PathVariable("no") Long cartNo) {
    cartService.removeFromCart(cartNo);
    return ResponseEntity.noContent().build();
  }
  
  // 3. 장바구니 목록 조회 (클라이언트 권한)
  @GetMapping
  public ResponseEntity<List<CartItemResponseDto>> getCartList() {
    Long userNo = 1L; // TODO: @AuthenticationPrincipal 로 교체 예정
    
    List<CartItemResponseDto> cartItems = cartService.findCartItems(userNo);
    return ResponseEntity.ok(cartItems);
  }
  
  // 결제 (클라이언트 권한)
  @PostMapping("/checkout")
  public ResponseEntity<CheckoutResponseDto> checkout(
      @RequestBody CheckoutRequestDto req
  ) {
    req.setUserNo(Optional.ofNullable(req.getUserNo()).orElse(1L));
    // TODO: 나중에 @AuthenticationPrincipal 로 대체
    CheckoutResponseDto dto = cartService.checkout(req);
    return ResponseEntity.ok(dto);
  }
  
  @PostMapping("/complete")
  public ResponseEntity<Void> completeOrder(
      @RequestBody OnlyOrderCodeDto req
  ) {
    System.out.println("😂😂😂😂😂😂orderCode = " + req.getOrderId());
    cartService.completeOrder(1L, req.getOrderId()); // TODO: 인증 정보로 대체 예정
    return ResponseEntity.ok().build();
  }
  
  // 장바구니 전체 삭제 (클라이언트 권한)
  @DeleteMapping("/all")
  public ResponseEntity<Void> clearCart() {
    System.out.println( "clearCart()");
    // TODO: 나중에 @AuthenticationPrincipal 로 대체
    Long userNo = 1L;
    cartService.deleteAllByUser(userNo);
    return ResponseEntity.noContent().build();
  }
}