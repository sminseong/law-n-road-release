package com.lawnroad.template.service;

import java.util.List;

public interface OcrService {
  String extractTextFromUrls(List<String> imageUrls);
}
