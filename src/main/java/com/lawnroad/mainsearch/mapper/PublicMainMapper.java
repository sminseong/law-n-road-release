package com.lawnroad.mainsearch.mapper;

import com.lawnroad.mainsearch.dto.LawyerAdBannerDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PublicMainMapper {
  // 메인 페이지 메인 베너 조회
  List<LawyerAdBannerDto> selectActiveMainBanners();
}
