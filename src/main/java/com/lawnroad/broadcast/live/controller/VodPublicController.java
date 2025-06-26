package com.lawnroad.broadcast.live.controller;

import com.lawnroad.broadcast.live.dto.*;
import com.lawnroad.broadcast.live.service.VodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/vod")
@RequiredArgsConstructor
public class VodPublicController {

    private final VodService vodService;

    @GetMapping("/list")
    public ResponseEntity<VodListResponseDto> getVodList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "recent") String sort,
            @RequestParam(required = false) Long categoryNo
    ) {
        VodListRequestDto requestDto = new VodListRequestDto();
        requestDto.setPage(page);
        requestDto.setSize(size);
        requestDto.setSort(sort);
        requestDto.setCategoryNo(categoryNo);

        VodListResponseDto response = vodService.getPagedVodList(requestDto);
        return ResponseEntity.ok(response);
    }

    // 조회수 증가
    @PutMapping("/{vodNo}")
    public ResponseEntity<Void> increaseViewCount(@PathVariable Long vodNo) {
        vodService.increaseViewCount(vodNo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/view/{broadcastNo}")
    public ResponseEntity<VodDetailDto> getVodDetail(@PathVariable Long broadcastNo) {
        VodDetailDto dto = vodService.getVodDetailByBroadcastNo(broadcastNo);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/all")
    public ResponseEntity<List<VodListItemDto>> getAllVodList() {
        List<VodListItemDto> vods = vodService.getAllVodList();
        System.out.println(vods);
        return ResponseEntity.ok(vods);
    }
}
