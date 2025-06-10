package com.lawnroad.broadcast.live.service;

import com.lawnroad.broadcast.live.dto.CategoryDto;
import com.lawnroad.broadcast.live.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryMapper.findAll().stream()
                .map(vo -> CategoryDto.builder()
                        .no(vo.getNo())
                        .name(vo.getName())
                        .createdAt(vo.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
