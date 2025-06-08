package com.lawnroad.template.mapper;

import com.lawnroad.template.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TemplateMapper {
  
  List<TemplateListDto> findAll();
  
  List<TemplateDto> findByUserNo(@Param("userNo") Long userNo);
  
  TemplateDto findByNoAndUserNo(@Param("no") Long no, @Param("userNo") Long userNo);
  
  List<TemplateListDto> findByLawyerNo(@Param("lawyerNo") Long lawyerNo);
  
  TemplateDto findByNoAndLawyerNo(@Param("no") Long no, @Param("lawyerNo") Long lawyerNo);
  
  void insert(@Param("dto") TemplateCreateDto dto);
  
  void update(@Param("no") Long no, @Param("lawyerNo") Long lawyerNo, @Param("dto") TemplateUpdateDto dto);
  
  void delete(@Param("no") Long no, @Param("lawyerNo") Long lawyerNo);
}