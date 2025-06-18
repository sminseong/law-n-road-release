package com.lawnroad.broadcast.live.controller;

import com.lawnroad.broadcast.live.dto.CategoryDto;
import com.lawnroad.broadcast.live.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/list")
    public ResponseEntity<List<CategoryDto>> getCategoryList() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
}
