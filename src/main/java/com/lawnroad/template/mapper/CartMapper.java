package com.lawnroad.template.mapper;


import com.lawnroad.template.dto.CartItemResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartMapper {
  
  // 1. 장바구니에 이미 담긴 템플릿인지 확인
  int existsByUserAndTemplate(@Param("userNo") Long userNo, @Param("tmplNo") Long tmplNo);
  
  // 2. 장바구니에 템플릿 추가
  void insertCart(@Param("userNo") Long userNo, @Param("tmplNo") Long tmplNo);
  
  // 3. 장바구니 템플릿 삭제
  void deleteByUserAndTemplate(@Param("userNo") Long userNo, @Param("tmplNo") Long tmplNo);
  
  // 4. 장바구니 목록 조회
  List<CartItemResponseDto> selectCartListByUser(@Param("userNo") Long userNo);
}