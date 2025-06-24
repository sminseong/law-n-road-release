package com.lawnroad.account.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LawyerPointMapper {
  
  // 포인트 증감 (음수도 허용)
  int updatePoint(@Param("lawyerNo") Long lawyerNo, @Param("amount") int amount);
}
