package com.lawnroad.template.controller;

import com.lawnroad.template.dto.CartAddRequestDto;
import com.lawnroad.template.dto.CartItemResponseDto;
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
    System.out.println("addToCart 호출 : " + dto);
    Long userNo = 1L; // TODO: @AuthenticationPrincipal 로 교체 예정
    
    boolean added = cartService.addToCart(userNo, dto.getTmplNo());
    if (added) {
      return ResponseEntity.status(201).build(); // Created
    } else {
      return ResponseEntity.status(409).build(); // Conflict (이미 있음)
    }
  }
  
  // 2. 장바구니 삭제
  @DeleteMapping("/{tmplNo}")
  public ResponseEntity<Void> removeFromCart(@PathVariable Long tmplNo) {
    System.out.println("removeFromCart 호출 : " + tmplNo);
    Long userNo = 1L; // TODO: @AuthenticationPrincipal 로 교체 예정
    
    cartService.removeFromCart(userNo, tmplNo);
    return ResponseEntity.noContent().build(); // 204
  }
  
  // 3. 장바구니 목록 조회
  @GetMapping
  public ResponseEntity<List<CartItemResponseDto>> getCartList() {
    System.out.println("getCartList 호출");
    Long userNo = 1L; // TODO: @AuthenticationPrincipal 로 교체 예정
    
    List<CartItemResponseDto> cartItems = cartService.findCartItems(userNo);
    return ResponseEntity.ok(cartItems);
  }
}