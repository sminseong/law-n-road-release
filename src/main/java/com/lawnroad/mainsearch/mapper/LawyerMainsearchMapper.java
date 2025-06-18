package com.lawnroad.mainsearch.mapper;

import com.lawnroad.mainsearch.dto.LawyerBasicInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LawyerMainsearchMapper {
  
  // 변호사 기본 정보 조회
  LawyerBasicInfoDto selectLawyerBasicInfoByNo(@Param("lawyerNo") Long lawyerNo);
}
