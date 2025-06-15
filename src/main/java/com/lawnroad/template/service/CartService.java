package com.lawnroad.template.service;

import com.lawnroad.template.dto.CartItemResponseDto;
import com.lawnroad.template.dto.CheckoutRequestDto;

import java.util.List;

public interface CartService {
  
  boolean addToCart(Long userNo, Long tmplNo);
  
  void removeFromCart(Long cartNo);
  
  List<CartItemResponseDto> findCartItems(Long userNo);
  
  Long checkout(CheckoutRequestDto dto);
  
  void deleteAllByUser(Long userNo);
}