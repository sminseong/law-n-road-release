package com.lawnroad.template.service;

import com.lawnroad.template.dto.cart.CartItemResponseDto;
import com.lawnroad.template.dto.cart.CheckoutRequestDto;
import com.lawnroad.template.dto.cart.CheckoutResponseDto;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CartService {

  boolean addToCart(Long userNo, Long tmplNo);

  void removeFromCart(Long cartNo);

  List<CartItemResponseDto> findCartItems(Long userNo);

  CheckoutResponseDto checkout(CheckoutRequestDto dto);
  
  void completeOrder(Long userNo, String orderCode);

  void deleteAllByUser(Long userNo);
}