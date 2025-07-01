package com.lawnroad.common.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.lang.Nullable;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
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
        String[] extensions = {".png", ".jpg", ".jpeg", ".ico", ".webp"};
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
  
  /**
   * 네이버 이미지 스토리지에 리사이징 해서 저장하기
   * @param file MultipartFile
   * @param dir "uploads" 디렉토리
   * @param oldFileUrl 이전 파일 URL (없으면 null)
   * @param resize resize (true, false)
   * @param width width
   * @param height height
   * @param optimize optimize (true, false)
   * @param targetFormat 변환 포맷 (없으면 null)
   * @param quality 품질 (0~1)
   * @return ncp 경로
   */
  public String saveOptimizedImage(MultipartFile file, String dir, @Nullable String oldFileUrl,
                                   boolean resize, int width, int height,
                                   boolean optimize, @Nullable String targetFormat, float quality) {
    try {
      String originalFilename = file.getOriginalFilename();
      String originalExt = getExtension(originalFilename);
      
      // targetFormat이 null이면 원본 확장자로 설정
      if (targetFormat == null || targetFormat.isBlank()) {
        targetFormat = originalExt.startsWith(".") ? originalExt.substring(1).toLowerCase() : originalExt.toLowerCase();
      }
      
      // 최종 확장자 (파일명용)
      String ext = optimize ? "." + targetFormat.toLowerCase() : originalExt;
      
      String baseName;
      if (oldFileUrl != null && oldFileUrl.contains("/")) {
        String oldKey = extractKeyFromUrl(oldFileUrl);
        baseName = oldKey.contains(".") ? oldKey.substring(0, oldKey.lastIndexOf('.')) : oldKey;
        deleteVariants(oldFileUrl, null);
        
//        String[] extensions = {".png", ".jpg", ".jpeg", ".ico", ".webp"};
//        for (String candidateExt : extensions) {
//          String candidateKey = baseName + candidateExt;
//          if (!candidateExt.equals(ext) && amazonS3.doesObjectExist(bucketName, candidateKey)) {
//            amazonS3.deleteObject(bucketName, candidateKey);
//          }
//        }
      } else {
        baseName = dir + "/" + UUID.randomUUID();
      }
      
      String finalKey = baseName + ext;
      String contentType = "image/" + targetFormat.toLowerCase();
      InputStream inputStream;
      long contentLength;
      
      boolean isImage = file.getContentType() != null && file.getContentType().startsWith("image");
      
      if (resize && isImage) {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnails.Builder<BufferedImage> builder = Thumbnails.of(originalImage).size(width, height);
        
        if (optimize) {
          builder.outputFormat(targetFormat.toLowerCase())
              .outputQuality(quality); // e.g., 0.7
        }
        
        builder.toOutputStream(os);
        byte[] optimizedBytes = os.toByteArray();
        inputStream = new ByteArrayInputStream(optimizedBytes);
        contentLength = optimizedBytes.length;
      } else {
        inputStream = file.getInputStream();
        contentLength = file.getSize();
      }
      
      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentLength(contentLength);
      metadata.setContentType(contentType);
      
      PutObjectRequest request = new PutObjectRequest(bucketName, finalKey, inputStream, metadata)
          .withCannedAcl(CannedAccessControlList.PublicRead);
      amazonS3.putObject(request);
      
      return BASE_URL + bucketName + "/" + finalKey;
      
    } catch (IOException e) {
      throw new RuntimeException("이미지 최적화 저장 실패", e);
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
    
    String[] extensions = {".png", ".jpg", ".jpeg", ".webp", ".ico"};
    
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
  
}