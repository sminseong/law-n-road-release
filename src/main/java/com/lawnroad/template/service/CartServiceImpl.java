package com.lawnroad.template.service;

import com.lawnroad.template.dto.CartItemResponseDto;
import com.lawnroad.template.mapper.CartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
  
  private final CartMapper cartMapper;
  
  @Override
  public boolean addToCart(Long userNo, Long tmplNo) {
    if (cartMapper.existsByUserAndTemplate(userNo, tmplNo) > 0) {
      return false; // 이미 담겨 있음
    }
    cartMapper.insertCart(userNo, tmplNo);
    return true;
  }
  
  @Override
  public void removeFromCart(Long cartNo) {
    cartMapper.deleteByCartNo(cartNo);
  }
  
  @Override
  public List<CartItemResponseDto> findCartItems(Long userNo) {
    return cartMapper.selectCartListByUser(userNo);
  }
}