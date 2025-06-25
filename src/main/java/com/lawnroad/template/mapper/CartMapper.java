package com.lawnroad.template.mapper;


import com.lawnroad.template.dto.cart.CartItemResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CartMapper {
  
  // 1. 장바구니에 이미 담긴 템플릿인지 확인
  int existsByUserAndTemplate(@Param("userNo") Long userNo, @Param("tmplNo") Long tmplNo);
  
  // 2. 장바구니에 템플릿 추가
  void insertCart(@Param("userNo") Long userNo, @Param("tmplNo") Long tmplNo);
  
  // 3. 장바구니 템플릿 삭제
  void deleteByCartNo(@Param("cartNo") Long cartNo);
  
  // 4. 장바구니 목록 조회
  List<CartItemResponseDto> selectCartListByUser(@Param("userNo") Long userNo);
  
  // 5. 장바구니 전체 삭제
  void deleteByUserNo(@Param("userNo") Long userNo);
  
  // 6. 주문상세에 추가하기
  void insertHistory(
      @Param("tmplNo") Long tmplNo,
      @Param("orderNo") Long orderNo,
      @Param("price") Integer price
  );
  
  @Select("SELECT no FROM orders WHERE order_code = #{orderCode}")
  Long selectOrderNoByOrderCode(@Param("orderCode") String orderCode);
  
  // 구매횟수 증가시키기
  void incrementSalesCount(@Param("tmplNo") Long tmplNo);
  
}