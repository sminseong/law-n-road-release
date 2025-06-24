package com.lawnroad.broadcast.live.controller;

import com.lawnroad.broadcast.live.dto.VodDetailDto;
import com.lawnroad.broadcast.live.dto.VodListDto;
import com.lawnroad.broadcast.live.dto.VodListRequestDto;
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
    public ResponseEntity<List<VodListDto>> getVodList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        VodListRequestDto requestDto = new VodListRequestDto();
        requestDto.setPage(page);
        requestDto.setSize(size);

        List<VodListDto> vodList = vodService.getPublicVodList(requestDto);
        System.out.println(vodList);
        return ResponseEntity.ok(vodList);
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
}
