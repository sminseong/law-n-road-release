package com.lawnroad.template.mapper;

import com.lawnroad.template.dto.TemplateDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
                               @Param("pathJson") String pathJson);
  
  // 내가 등록한 템플릿 목록 조회
  List<TemplateDto> selectMyTemplates(@Param("lawyerNo") Long lawyerNo,
                                      @Param("offset") int offset,
                                      @Param("limit") int limit,
                                      @Param("categoryNo") Long categoryNo,
                                      @Param("keyword") String keyword,
                                      @Param("type") String type,
                                      @Param("sort") String sort);
  
  // 내가 등록한 템플릿 총 개수 조회
  int countMyTemplates(@Param("lawyerNo") Long lawyerNo,
                       @Param("categoryNo") Long categoryNo,
                       @Param("keyword") String keyword,
                       @Param("type") String type);
  
  // 템플릿 삭제
  int markTemplateAsDeleted(@Param("templateNo") Long templateNo);
}

