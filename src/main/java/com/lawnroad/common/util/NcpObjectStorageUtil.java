package com.lawnroad.common.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.lang.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NcpObjectStorageUtil {
  
  private final AmazonS3 amazonS3;
  
  @Value("${ncp.storage.bucket}")
  private String bucketName;
  
  private static final String BASE_URL = "https://kr.object.ncloudstorage.com/";
  
  /**
   * NCP 오브젝트 스토리지에 저장. oldFileUrl이 주어지면 baseName 유지하며 확장자만 새로 적용
   */
  public String save(MultipartFile file, String dir, @Nullable String oldFileUrl) {
    try {
      String ext = getExtension(file.getOriginalFilename());
      
      String baseName;
      if (oldFileUrl != null && oldFileUrl.contains("/")) {
        // oldFileUrl에서 baseName 추출
        String oldKey = extractKeyFromUrl(oldFileUrl);
        baseName = oldKey.contains(".") ? oldKey.substring(0, oldKey.lastIndexOf('.')) : oldKey;
        
        // 동일 baseName의 다른 확장자 삭제
        String[] extensions = { ".png", ".jpg", ".jpeg", ".ico", ".webp" };
        for (String candidateExt : extensions) {
          String candidateKey = baseName + candidateExt;
          if (!candidateExt.equals(ext) && amazonS3.doesObjectExist(bucketName, candidateKey)) {
            amazonS3.deleteObject(bucketName, candidateKey);
          }
        }
      } else {
        // UUID로 새 baseName 생성
        baseName = dir + "/" + UUID.randomUUID();
      }
      
      String finalKey = baseName + ext;
      
      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentLength(file.getSize());
      metadata.setContentType(file.getContentType());
      
      InputStream inputStream = file.getInputStream();
      
      PutObjectRequest request = new PutObjectRequest(bucketName, finalKey, inputStream, metadata)
          .withCannedAcl(CannedAccessControlList.PublicRead);
      
      amazonS3.putObject(request);
      
      return BASE_URL + bucketName + "/" + finalKey;
    } catch (IOException e) {
      throw new RuntimeException("파일 저장 중 오류 발생", e);
    }
  }
  
  public void delete(String key) {
    amazonS3.deleteObject(bucketName, key);
  }
  
  public void deleteVariants(String fileUrl, @Nullable String protectedFileUrl) {
    if (fileUrl == null || fileUrl.isBlank()) return;
    
    // baseName 추출
    String baseKey = extractKeyFromUrl(fileUrl);
    if (baseKey.isBlank()) return;
    baseKey = baseKey.contains(".") ? baseKey.substring(0, baseKey.lastIndexOf('.')) : baseKey;
    
    String[] extensions = { ".png", ".jpg", ".jpeg", ".webp", ".ico" };
    
    String protectedKey = (protectedFileUrl != null && !protectedFileUrl.isBlank())
        ? extractKeyFromUrl(protectedFileUrl)
        : null;
    
    for (String ext : extensions) {
      String candidateKey = baseKey + ext;
      
      // 보호 대상이면 삭제 생략
      if (candidateKey.equals(protectedKey)) continue;
      
      if (amazonS3.doesObjectExist(bucketName, candidateKey)) {
        amazonS3.deleteObject(bucketName, candidateKey);
      }
    }
  }
  
  private String getExtension(String filename) {
    return (filename != null && filename.contains("."))
        ? filename.substring(filename.lastIndexOf('.'))
        : "";
  }
  
  private String extractKeyFromUrl(String url) {
    if (url == null || !url.contains(bucketName)) return "";
    return url.substring(url.indexOf(bucketName) + bucketName.length() + 1); // +1 for /
  }
  
  public void debugLogBucket(String bucketName) {
    System.out.println("=== 버킷 전체 내용 확인 ===");
    System.out.println("버킷: " + bucketName);
    
    try {
      ObjectListing listing = amazonS3.listObjects(bucketName);
      System.out.println("전체 객체 수: " + listing.getObjectSummaries().size());
      
      int logFileCount = 0;
      for (S3ObjectSummary obj : listing.getObjectSummaries()) {
        if (obj.getKey().startsWith("law-n-road-log-bucket")) {
          logFileCount++;
          System.out.println("🔍 로그 파일 발견: " + obj.getKey() +
              " | 크기: " + obj.getSize() +
              " | 수정일: " + obj.getLastModified());
        } else {
          System.out.println("📁 일반 파일: " + obj.getKey());
        }
      }
      
      System.out.println("총 로그 파일 수: " + logFileCount);
      
    } catch (Exception e) {
      System.err.println("오류: " + e.getMessage());
      e.printStackTrace();
    }
  }
  
  private Set<String> parseAccessLogs(String bucketName, int logLookbackDays) {
    Set<String> accessed = new HashSet<>();
    
    System.out.println("=== 로그 파싱 시작 ===");
    System.out.println("대상 버킷: " + bucketName);
    
    try {
      // 같은 버킷에서 로그 prefix로 필터링
      ObjectListing logs = amazonS3.listObjects(new ListObjectsRequest()
          .withBucketName(bucketName)
          .withPrefix("law-n-road-log-bucket/"));
      
      System.out.println("로그 prefix로 찾은 파일 수: " + logs.getObjectSummaries().size());
      
      long now = System.currentTimeMillis();
      long logThreshold = logLookbackDays * 24L * 60 * 60 * 1000;
      
      for (S3ObjectSummary logFile : logs.getObjectSummaries()) {
        System.out.println("로그 파일 검사: " + logFile.getKey());
        System.out.println("파일 크기: " + logFile.getSize() + " bytes");
        System.out.println("수정일: " + logFile.getLastModified());
        
        long logAge = now - logFile.getLastModified().getTime();
        
        if (logAge > logThreshold) {
          System.out.println("⏭️ 너무 오래된 로그, 건너뛰기");
          continue;
        }
        
        // 로그 파일이 비어있지 않은지 확인
        if (logFile.getSize() == 0) {
          System.out.println("⚠️ 빈 로그 파일, 건너뛰기");
          continue;
        }
        
        System.out.println("📖 로그 파싱 중...");
        
        try (S3Object obj = amazonS3.getObject(bucketName, logFile.getKey());
             BufferedReader reader = new BufferedReader(new InputStreamReader(obj.getObjectContent()))) {
          
          String line;
          int lineCount = 0;
          int accessedCount = 0;
          
          while ((line = reader.readLine()) != null) {
            lineCount++;
            
            // 처음 3줄 출력해서 로그 형식 확인
            if (lineCount <= 3) {
              System.out.println("로그 라인 " + lineCount + ": " + line);
            }
            
            // === 실제 로그 파싱 로직 ===
            // NCP Object Storage 로그는 공백으로 구분됨
            String[] parts = line.split("\\s+");
            if (parts.length >= 9) { // 최소 9개 필드 필요
              try {
                String operation = parts.length > 7 ? parts[7] : ""; // REST.GET.OBJECT 등
                String key = parts.length > 8 ? parts[8] : ""; // 객체 키
                
                // GET, HEAD 요청만 접근으로 간주
                if (key != null && !key.equals("-") &&
                    (operation.contains("GET") || operation.contains("HEAD"))) {
                  
                  // URL 디코딩
                  key = java.net.URLDecoder.decode(key, "UTF-8");
                  
                  // 따옴표 제거
                  if (key.startsWith("\"") && key.endsWith("\"")) {
                    key = key.substring(1, key.length() - 1);
                  }
                  
                  // 로그 파일 자체는 제외
                  if (!key.startsWith("law-n-road-log-bucket/")) {
                    accessed.add(key);
                    accessedCount++;
                    
                    // 처음 몇 개 접근 키 출력
                    if (accessedCount <= 5) {
                      System.out.println("접근된 파일: " + key);
                    }
                  }
                }
              } catch (Exception e) {
                System.err.println("라인 파싱 오류: " + e.getMessage() + " | 라인: " + line);
              }
            } else {
              // 필드 수가 부족한 경우 (첫 번째 로그 파일에서만 출력)
              if (lineCount <= 3) {
                System.out.println("필드 수 부족 (expected >=9, actual=" + parts.length + "): " + line);
              }
            }
          }
          
          System.out.println("총 라인 수: " + lineCount);
          System.out.println("이 파일에서 접근된 파일 수: " + accessedCount);
          
        } catch (Exception e) {
          System.err.println("로그 파싱 오류: " + e.getMessage());
          e.printStackTrace();
        }
      }
      
    } catch (Exception e) {
      System.err.println("로그 조회 오류: " + e.getMessage());
      e.printStackTrace();
    }
    
    System.out.println("=== 로그 파싱 완료 ===");
    System.out.println("접근된 고유 파일 수: " + accessed.size());
    
    return accessed;
  }
  
  // 접근 로그 기반 오래된 객체 자동 삭제
  public void cleanupUnusedObjectsFromLogs(String logBucketName, int logLookbackDays, int fileAgeThresholdDays) {
    // 안전장치
    if (fileAgeThresholdDays < 1) {
      System.out.println("파일 나이 임계값이 너무 작습니다. 최소 1일로 설정합니다.");
      fileAgeThresholdDays = 1;
    }
    
    System.out.println("=== 파일 정리 작업 시작 ===");
    System.out.println("로그 확인 기간: " + logLookbackDays + "일");
    System.out.println("파일 삭제 기준: " + fileAgeThresholdDays + "일 이상");
    
    // 최근 N일간 접근된 파일 목록 수집
    Set<String> accessedKeys = parseAccessLogs(logBucketName, logLookbackDays);
    System.out.println("최근 " + logLookbackDays + "일간 접근된 파일 개수: " + accessedKeys.size());
    
    // 모든 파일 목록 가져오기
    ObjectListing listing = amazonS3.listObjects(bucketName);
    
    long now = System.currentTimeMillis();
    long threshold = fileAgeThresholdDays * 24L * 60 * 60 * 1000;
    
    int totalFiles = 0;
    int oldFiles = 0;
    int deletedFiles = 0;
    
    for (S3ObjectSummary obj : listing.getObjectSummaries()) {
      String key = obj.getKey();
      long lastModified = obj.getLastModified().getTime();
      long age = now - lastModified;
      boolean isOld = age > threshold;
      boolean isAccessed = accessedKeys.contains(key);
      
      totalFiles++;
      
      System.out.println("=== 파일 검사 ===");
      System.out.println("파일: " + key);
      System.out.println("생성일: " + obj.getLastModified());
      System.out.println("나이(일): " + (age / (24 * 60 * 60 * 1000)));
      System.out.println("오래됨? " + isOld);
      System.out.println("최근 접근? " + isAccessed);
      
      if (isOld) {
        oldFiles++;
        if (!isAccessed) {
          // 오래되었고 + 최근에 접근되지 않은 파일 삭제
          amazonS3.deleteObject(bucketName, key);
          deletedFiles++;
          System.out.println("🗑️ 삭제됨: " + key);
        } else {
          System.out.println("✅ 보존됨 (최근 접근): " + key);
        }
      } else {
        System.out.println("✅ 보존됨 (신규 파일): " + key);
      }
      System.out.println();
    }
    
    System.out.println("=== 정리 작업 완료 ===");
    System.out.println("전체 파일: " + totalFiles);
    System.out.println("오래된 파일: " + oldFiles);
    System.out.println("삭제된 파일: " + deletedFiles);
  }

//  // 로그 파서 - S3 액세스 로그 형식에 맞게 수정
//  private Set<String> parseAccessLogs(String logBucketName, int logLookbackDays) {
//    Set<String> accessed = new HashSet<>();
//
//    ObjectListing logs = amazonS3.listObjects(new ListObjectsRequest()
//        .withBucketName(logBucketName)
//        .withPrefix("law-n-road-log-bucket/"));
//
//    long now = System.currentTimeMillis();
//    long logThreshold = logLookbackDays * 24L * 60 * 60 * 1000;
//
//    System.out.println("=== 로그 파싱 시작 ===");
//
//    for (S3ObjectSummary logFile : logs.getObjectSummaries()) {
//      long logAge = now - logFile.getLastModified().getTime();
//
//      // 지정된 기간보다 오래된 로그는 건너뛰기
//      if (logAge > logThreshold) {
//        System.out.println("오래된 로그 건너뛰기: " + logFile.getKey());
//        continue;
//      }
//
//      System.out.println("로그 파싱 중: " + logFile.getKey());
//
//      try (S3Object obj = amazonS3.getObject(logBucketName, logFile.getKey());
//           BufferedReader reader = new BufferedReader(new InputStreamReader(obj.getObjectContent()))) {
//
//        String line;
//        int lineCount = 0;
//        while ((line = reader.readLine()) != null) {
//          lineCount++;
//
//          // S3 액세스 로그는 공백으로 구분됨
//          String[] parts = line.split("\\s+");
//          if (parts.length >= 8) {
//            String operation = parts[7]; // REST.GET.OBJECT, REST.HEAD.OBJECT 등
//            String key = parts[8]; // 객체 키
//
//            if (key != null && !key.equals("-") &&
//                (operation.contains("GET") || operation.contains("HEAD"))) {
//              try {
//                // URL 디코딩
//                key = java.net.URLDecoder.decode(key, "UTF-8");
//                // 따옴표 제거
//                if (key.startsWith("\"") && key.endsWith("\"")) {
//                  key = key.substring(1, key.length() - 1);
//                }
//                accessed.add(key);
//              } catch (Exception e) {
//                System.err.println("키 디코딩 실패: " + key);
//              }
//            }
//          }
//        }
//        System.out.println("로그 라인 수: " + lineCount);
//
//      } catch (Exception e) {
//        System.err.println("로그 파싱 중 오류: " + logFile.getKey() + " - " + e.getMessage());
//      }
//    }
//
//    System.out.println("=== 로그 파싱 완료 ===");
//    System.out.println("접근된 고유 파일 수: " + accessed.size());
//
//    return accessed;
//  }
}