package com.lawnroad.mainsearch.controller;

import com.lawnroad.mainsearch.dto.BoardDto;
import com.lawnroad.mainsearch.dto.LawyerAdBannerDto;
import com.lawnroad.mainsearch.service.PublicMainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/main")
@RequiredArgsConstructor
public class PublicMainController {
  
  private final PublicMainService publicMainService;
  
  @GetMapping("/main-banners")
  public List<LawyerAdBannerDto> getMainBanners() {
    return publicMainService.getActiveMainBanners();
  }
  
  @GetMapping("/latest")
  public List<BoardDto> getLatestBoards() {
    return publicMainService.getLatestBoards();
  }
}
