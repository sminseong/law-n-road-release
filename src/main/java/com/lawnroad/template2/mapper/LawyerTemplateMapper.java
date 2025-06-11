package com.lawnroad.template2.mapper;

import com.lawnroad.template2.dto.TemplateDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LawyerTemplateMapper {
  // 템플릿 공통 등록 (user_no, category_no 등 포함)
  void insertTemplate(TemplateDto dto);
  
  // 에디터 기반 추가 정보 등록
  void insertEditorBasedTemplate(@Param("no") Long templateNo,
                                 @Param("content") String content,
                                 @Param("varJson") String varJson,
                                 @Param("aiEnabled") Integer aiEnabled);
  
  // 파일 기반 추가 정보 등록
  void insertFileBasedTemplate(@Param("no") Long templateNo,
                               @Param("path") String path);
}
