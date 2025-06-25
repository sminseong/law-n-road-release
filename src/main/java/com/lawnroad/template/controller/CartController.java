package com.lawnroad.template.controller;

import com.lawnroad.common.util.JwtTokenUtil;
import com.lawnroad.template.dto.cart.*;
import com.lawnroad.template.service.CartService;
import io.jsonwebtoken.Claims;
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
  private final JwtTokenUtil jwtUtil;
  
  // 1. 장바구니 추가 (클라이언트 권한)
  @PostMapping
  public ResponseEntity<Void> addToCart(@RequestHeader("Authorization") String authHeader, @RequestBody CartAddRequestDto dto) {
    String token = authHeader.replace("Bearer ", "");
    Claims claims = jwtUtil.parseToken(token);
    long userNo = claims.get("no", Long.class);
    
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
  public ResponseEntity<List<CartItemResponseDto>> getCartList(@RequestHeader("Authorization") String authHeader) {
    String token = authHeader.replace("Bearer ", "");
    Claims claims = jwtUtil.parseToken(token);
    long userNo = claims.get("no", Long.class);
    
    List<CartItemResponseDto> cartItems = cartService.findCartItems(userNo);
    return ResponseEntity.ok(cartItems);
  }
  
  // 결제 (클라이언트 권한)
  @PostMapping("/checkout")
  public ResponseEntity<CheckoutResponseDto> checkout(
      @RequestHeader("Authorization") String authHeader,
      @RequestBody CheckoutRequestDto req
  ) {
    String token = authHeader.replace("Bearer ", "");
    Claims claims = jwtUtil.parseToken(token);
    long userNo = claims.get("no", Long.class);
    
    req.setUserNo(Optional.ofNullable(req.getUserNo()).orElse(userNo));
    CheckoutResponseDto dto = cartService.checkout(req);
    return ResponseEntity.ok(dto);
  }
  
  // 결제 후 사후처리작업 (장바구니 비우거나, 히스토리 옮기거나 등)
  @PostMapping("/complete")
  public ResponseEntity<Void> completeOrder(
      @RequestHeader("Authorization") String authHeader,
      @RequestBody OnlyOrderCodeDto req
  ) {
    String token = authHeader.replace("Bearer ", "");
    Claims claims = jwtUtil.parseToken(token);
    long userNo = claims.get("no", Long.class);
    
    cartService.completeOrder(userNo, req.getOrderId());
    return ResponseEntity.ok().build();
  }
  
  // 장바구니 전체 삭제 (클라이언트 권한)
  @DeleteMapping("/all")
  public ResponseEntity<Void> clearCart(@RequestHeader("Authorization") String authHeader) {
    String token = authHeader.replace("Bearer ", "");
    Claims claims = jwtUtil.parseToken(token);
    long userNo = claims.get("no", Long.class);
    
    cartService.deleteAllByUser(userNo);
    return ResponseEntity.noContent().build();
  }
}