package com.lawnroad.template.service;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OcrServiceImpl implements OcrService {
  
  // NCP OCR API 호출에 필요한 시크릿 키 (X-OCR-SECRET)
  @Value("${ncp.ocr.secretKey}")
  private String ocrSecret;
  
  // NCP OCR API 호출 엔드포인트 (예: https://...apigw.ntruss.com/.../general)
  @Value("${ncp.ocr.endpoint}")
  private String ocrEndpoint;
  
  // HTTP 요청을 위한 RestTemplate (※ 현재 직접 생성됨, 운영 시 Bean 주입 권장)
  private final RestTemplate restTemplate; // @RequiredArgsConstructor로 주입됨
  
  /**
   * NCP OCR API를 사용하여 여러 PDF URL에서 텍스트를 추출하고, 하나의 문자열로 병합해서 반환.
   *
   * @param imageUrls OCR을 수행할 PDF 파일들의 URL 리스트
   * @return OCR을 통해 추출된 전체 텍스트 (파일, 페이지 구분 없음)
   */
  @Override
  public String extractTextFromUrls(List<String> imageUrls) {
    StringBuilder totalText = new StringBuilder();
    
    int maxPages = Math.min(imageUrls.size(), 10); // 요청 구분용 인덱스 (requestId에 사용)
    for (int idx = 0; idx < maxPages; idx++) {
      String imageUrl = imageUrls.get(idx);
      try {
        // --- 1. OCR 요청 JSON Body 구성 ---
        JSONObject requestBody = new JSONObject();
        requestBody.put("version", "V1"); // OCR API 버전
        requestBody.put("requestId", "req_" + idx); // 고유 요청 ID
        requestBody.put("timestamp", System.currentTimeMillis()); // 현재 시간 (ms 단위)
        requestBody.put("lang", "ko"); // 한국어 인식 요청
        requestBody.put("resultType", "string"); // 결과는 inferText 기반
        
        // 추출할 대상의 정보 구성 (PDF 기준이므로 format은 고정)
        JSONArray images = new JSONArray();
        JSONObject image = new JSONObject();
        image.put("format", "pdf"); // 고정: PDF 파일
        image.put("name", "page_" + idx); // 페이지 식별자용 이름
        image.put("url", imageUrl); // Object Storage 등에서 접근 가능한 공개 URL
        image.put("pdfPageRange", "1-10"); // 한 URL로 들어온 PDF에 대해서도 최대 10페이지만 처리되도록 명시
        images.put(image);
        
        requestBody.put("images", images);
        
        // --- 2. HTTP 헤더 구성 ---
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // JSON 본문
        headers.set("X-OCR-SECRET", ocrSecret); // 인증 시크릿 키
        
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
        
        // --- 3. OCR API 호출 ---
        ResponseEntity<String> response = restTemplate.postForEntity(
            ocrEndpoint,
            entity,
            String.class
        );
        
        // --- 4. 응답 유효성 검사 ---
        if (!response.getStatusCode().is2xxSuccessful()) {
          throw new RuntimeException("OCR 호출 실패: HTTP " + response.getStatusCode());
        }
        
        // --- 5. OCR 결과 파싱 ---
        JSONObject json = new JSONObject(response.getBody());
        JSONArray imageResults = json.getJSONArray("images");
        // System.out.println("✅ OCR 응답 이미지 수: " + imageResults.length());
        
        for (int i = 0; i < imageResults.length(); i++) {
          JSONArray fields = imageResults.getJSONObject(i).optJSONArray("fields");
          
          if (fields != null) {
            for (int j = 0; j < fields.length(); j++) {
              String text = fields.getJSONObject(j).getString("inferText");
              totalText.append(text).append(" ");
            }
          }
        }
        
      } catch (Exception e) {
        // 예외 발생 시 로그 출력 (운영 시 log.warn/에러 수집 필요)
        System.err.println("⚠️ OCR 실패 (page " + (idx + 1) + "): " + e.getMessage());
      }
    }
    
    // 최종 결과: OCR로 읽은 모든 텍스트를 공백으로 구분해 하나의 문자열로 반환
    return totalText.toString().trim();
  }
}
