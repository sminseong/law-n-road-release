package com.lawnroad.broadcast.live.service;

import com.lawnroad.broadcast.live.dto.VodDetailDto;
import com.lawnroad.broadcast.live.dto.VodListDto;
import com.lawnroad.broadcast.live.dto.VodListRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VodService {
    void saveVodFile(Long broadcastNo, MultipartFile file, Integer duration, Long userNo) throws Exception;
    List<VodListDto> getPublicVodList(VodListRequestDto requestDto);
    void increaseViewCount(Long vodNo);

    // vod 상세
    VodDetailDto getVodDetailByBroadcastNo(Long broadcastNo);
}
