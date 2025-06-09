package com.lawnroad.common.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class FileStorageUtil {
  
  /**
   * 파일 저장 및 정적 리소스 경로 반환 (정상 동작을 위해 프로젝트 루트 기준으로 저장)
   *
   * @param file MultipartFile
   * @param relativeDir 상대 경로 (e.g., "uploads/images")
   * @return 정적 리소스 접근용 경로 (e.g., "/uploads/images/abc.png")
   */
  public String save(MultipartFile file, String relativeDir) {
    if (file.isEmpty()) {
      throw new IllegalArgumentException("빈 파일은 저장할 수 없습니다.");
    }
    
    try {
      // 프로젝트 루트 기준 절대 경로
      String basePath = new File("").getAbsolutePath();
      String normalizedDir = relativeDir.replace("\\", "/");
      if (!normalizedDir.endsWith("/")) normalizedDir += "/";
      
      String fullDirPath = basePath + File.separator + normalizedDir;
      File dir = new File(fullDirPath);
      if (!dir.exists() && !dir.mkdirs()) {
        throw new IOException("디렉토리 생성 실패: " + dir.getAbsolutePath());
      }
      
      String originalFilename = file.getOriginalFilename();
      String ext = originalFilename != null && originalFilename.contains(".")
          ? originalFilename.substring(originalFilename.lastIndexOf('.'))
          : "";
      String savedName = UUID.randomUUID() + ext;
      
      File targetFile = new File(fullDirPath + savedName);
      file.transferTo(targetFile);
      
      return "/" + normalizedDir + savedName;
      
    } catch (IOException e) {
      throw new RuntimeException("파일 저장 중 오류 발생", e);
    }
  }
}