package com.lawnroad.broadcast.live.mapper;

import com.lawnroad.broadcast.live.model.CategoryVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<CategoryVo> findAll();
}
