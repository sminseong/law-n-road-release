package com.lawnroad.common.util;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class FileStorageUtil {
  
  /**
   * 파일을 지정한 상대 디렉토리에 저장하고, 정적 리소스 접근용 경로를 반환합니다.
   * oldFilePath가 주어지면 해당 경로의 파일명 (baseName)과 동일한 이름으로 저장하되,
   * 확장자는 새로 업로드된 파일 기준으로 적용합니다.
   *
   * @param file MultipartFile 업로드된 파일
   * @param relativeDir 상대 저장 경로 (예: "uploads/images")
   * @param oldFilePath 이전 썸네일 경로 (동일 baseName 유지 목적, 확장자 무시)
   * @return 정적 리소스 접근용 경로 (예: "/uploads/images/abc.png")
   */
  public String save(MultipartFile file, String relativeDir, @Nullable String oldFilePath) {
    if (file.isEmpty()) {
      throw new IllegalArgumentException("빈 파일은 저장할 수 없습니다.");
    }
    
    try {
      // 절대 경로 계산
      String basePath = new File("").getAbsolutePath();
      String normalizedDir = relativeDir.replace("\\", "/");
      if (!normalizedDir.endsWith("/")) normalizedDir += "/";
      
      // 전체 디렉토리 경로 생성
      String fullDirPath = basePath + File.separator + normalizedDir;
      File dir = new File(fullDirPath);
      if (!dir.exists() && !dir.mkdirs()) {
        throw new IOException("디렉토리 생성 실패: " + dir.getAbsolutePath());
      }
      
      // 파일 확장자 추출 (.png, .jpg 등)
      String originalFilename = file.getOriginalFilename();
      String ext = originalFilename != null && originalFilename.contains(".")
          ? originalFilename.substring(originalFilename.lastIndexOf('.'))
          : "";
      
      // 저장 파일 이름 설정
      // 기존 이름을 유지하되, 확장자는 새로 들어온 파일 기준으로 덮어씌우기
      String savedName;
      if (oldFilePath != null && oldFilePath.contains("/")) {
        // 기존 경로에서 baseName 추출하여 확장자만 새로 적용
        String baseName = oldFilePath.substring(oldFilePath.lastIndexOf('/') + 1, oldFilePath.lastIndexOf('.'));
        savedName = baseName + ext; // 새 확장자 붙이기
      } else {
        // 새로 UUID로 파일명 생성
        savedName = UUID.randomUUID() + ext;
      }
      
      // 최종 저장 파일 경로
      File targetFile = new File(fullDirPath + savedName);
      file.transferTo(targetFile);
      
      // 정적 접근용 경로 반환
      return "/" + normalizedDir + savedName;
      
    } catch (IOException e) {
      throw new RuntimeException("파일 저장 중 오류 발생", e);
    }
  }
  
  /**
   * 지정한 oldWebPath 경로 기반으로 baseName을 추출하여,
   * 해당 baseName에 연결된 모든 확장자 파일(.png, .jpg 등)을 삭제합니다.
   * 단, protectedWebPath로 지정된 경로는 삭제하지 않습니다.
   *
   * @param oldWebPath 삭제 기준이 되는 경로 (URL 혹은 정적 리소스 경로 가능)
   * @param protectedWebPath 삭제하면 안 되는 보호 대상 경로 (URL 혹은 리소스 경로)
   */
  public void delete(String oldWebPath, @Nullable String protectedWebPath) {
    try {
      if (oldWebPath == null || oldWebPath.isBlank()) return;
      
      // URL일 경우 호스트 부분 제거 (http://localhost:8080 → 제거)
      String cleanedOldPath = oldWebPath.replaceFirst("^https?://[^/]+", "");
      String cleanedProtectedPath = protectedWebPath != null ? protectedWebPath.replaceFirst("^https?://[^/]+", "") : null;
      
      // 파일 경로 계산
      String basePath = new File("").getAbsolutePath();
      String relativePath = cleanedOldPath.replaceFirst("^/", "").replace("/", File.separator);
      
      // 삭제 대상 디렉토리 및 파일명
      File fullFile = new File(basePath + File.separator + relativePath);
      String dir = fullFile.getParent();
      String filename = fullFile.getName();
      String baseName = filename.contains(".") ? filename.substring(0, filename.lastIndexOf('.')) : filename;
      
      // 삭제 대상 확장자 목록
      String[] extensions = { ".png", ".jpg", ".jpeg", ".ico", ".webp" };
      
      for (String ext : extensions) {
        File variant = new File(dir, baseName + ext);
        String variantWebPath = "/uploads/images/" + baseName + ext;
        
        // 보호 대상이면 삭제 생략
        if (variantWebPath.equals(cleanedProtectedPath)) {
          // 삭제 실패 시 로그 출력 가능
//          System.out.println("보호된 파일 → 삭제 생략: " + variant.getAbsolutePath());
          continue;
        }
        
        if (variant.exists()) {
          boolean deleted = variant.delete();
//          System.out.println("삭제 시도: " + variant.getAbsolutePath() + " → " + (deleted ? "성공" : "실패"));
        }
      }
      
    } catch (Exception e) {
      System.err.println("파일 삭제 예외 발생: " + e.getMessage());
    }
  }
}