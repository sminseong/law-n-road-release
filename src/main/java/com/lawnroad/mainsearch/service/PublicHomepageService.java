package com.lawnroad.mainsearch.service;

import com.lawnroad.mainsearch.dto.LawyerHomepageDto;

public interface PublicHomepageService {
  LawyerHomepageDto getLawyerHomepage(Long lawyerNo);
}
