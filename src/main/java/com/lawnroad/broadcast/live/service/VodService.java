package com.lawnroad.broadcast.live.service;

import com.lawnroad.broadcast.live.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VodService {
    void saveVodFile(Long broadcastNo, MultipartFile file, Integer duration, Long userNo) throws Exception;
    VodListResponseDto getPagedVodList(VodListRequestDto requestDto);
    void increaseViewCount(Long vodNo);

    // vod 상세
    VodDetailDto getVodDetailByBroadcastNo(Long broadcastNo);
    // 홈 vod
    List<VodListItemDto> getAllVodList();
}
