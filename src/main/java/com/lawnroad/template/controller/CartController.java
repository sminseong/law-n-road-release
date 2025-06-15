package com.lawnroad.template.controller;

import com.lawnroad.template.dto.cart.CartAddRequestDto;
import com.lawnroad.template.dto.cart.CartItemResponseDto;
import com.lawnroad.template.dto.cart.CheckoutRequestDto;
import com.lawnroad.template.dto.cart.CheckoutResponseDto;
import com.lawnroad.template.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {
  
  private final CartService cartService;
  
  // 1. 장바구니 추가
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
  
  // 2. 장바구니 삭제 (cart row PK 기준)
  @DeleteMapping("/{no}")
  public ResponseEntity<Void> removeFromCart(@PathVariable("no") Long cartNo) {
    cartService.removeFromCart(cartNo);
    return ResponseEntity.noContent().build();
  }
  
  // 3. 장바구니 목록 조회
  @GetMapping
  public ResponseEntity<List<CartItemResponseDto>> getCartList() {
    Long userNo = 1L; // TODO: @AuthenticationPrincipal 로 교체 예정
    
    List<CartItemResponseDto> cartItems = cartService.findCartItems(userNo);
    return ResponseEntity.ok(cartItems);
  }
  
  @PostMapping("/aa")
  public ResponseEntity<CheckoutResponseDto> checkout(
      @RequestBody CheckoutRequestDto req
  ) {
    // TODO: 나중에 @AuthenticationPrincipal 로 대체
    req.setUserNo(1L);
    Long orderNo = cartService.checkout(req);
    return ResponseEntity.ok(new CheckoutResponseDto(orderNo));
  }
  
  // 장바구니 전체 삭제
  @DeleteMapping("/all")
  public ResponseEntity<Void> clearCart() {
    System.out.println( "clearCart()");
    // TODO: 나중에 @AuthenticationPrincipal 로 대체
    Long userNo = 1L;
    cartService.deleteAllByUser(userNo);
    return ResponseEntity.noContent().build();
  }
}