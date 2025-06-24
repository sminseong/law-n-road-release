package com.lawnroad.common.config;

import com.lawnroad.common.util.NcpObjectStorageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class SchedulerConfig {
  
  private final NcpObjectStorageUtil ncpObjectStorageUtil;
  
//  // 매일 새벽 4시에 정리 실행
//  // 초 분 시 일 월 요일 (* : 모든 값)
//  // */5 * * * * ?	-> 매 5초 간격으로 실행
//  // 0 0/30 * * * ? -> 매 30분 간격으로 실행
//  @Scheduled(cron = "*/1 * * * * ?")
//  public void cleanupOldObjectsDaily() {
//    System.out.println("스케줄러 실행됨");
//    ncpObjectStorageUtil.cleanupUnusedObjectsFromLogs("law-n-road", 30, 4);
//  }
  
  
  @Scheduled(cron = "*/1 * * * * ?")
  public void cleanupOldObjectsDaily() {
    System.out.println("=== 디버깅 시작 ===");
    
    // 실제 버킷에서 로그 prefix 확인
    ncpObjectStorageUtil.debugLogBucket("law-n-road");
    
    // 실제 정리 작업
    ncpObjectStorageUtil.cleanupUnusedObjectsFromLogs("law-n-road", 100, 1);
  }
}