package com.lawnroad.broadcast.live.controller;

import com.lawnroad.broadcast.live.dto.ViewerCountDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
@Slf4j
public class ViewerCountController {
    // 간단히 메모리 캐시
    private final ConcurrentHashMap<Long,Integer> countCache = new ConcurrentHashMap<>();

    /** 클라이언트 푸시: viewerCount 갱신 */
    @PostMapping("/lawyer/broadcast/viewer-count/update")
    public ResponseEntity<Void> updateViewerCount(@RequestBody ViewerCountDto dto) {
        countCache.put(dto.getBroadcastNo(), dto.getViewerCount());
        log.debug("Updated viewerCount: broadcastNo={} → {}", dto.getBroadcastNo(), dto.getViewerCount());
        return ResponseEntity.ok().build();
    }

    /** 다른 프론트나 백엔드가 조회할 엔드포인트 */
    @GetMapping("/public/broadcast/{broadcastNo}/viewer-count")
    public ResponseEntity<Integer> getViewerCount(@PathVariable Long broadcastNo) {
        Integer count = countCache.getOrDefault(broadcastNo, 0);
        return ResponseEntity.ok(count);
    }
}
