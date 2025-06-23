package com.lawnroad.broadcast.live.service;

import com.lawnroad.broadcast.live.mapper.VodMapper;
import com.lawnroad.broadcast.live.model.BroadcastVodVo;
import com.lawnroad.common.util.FileStorageUtil;
import com.lawnroad.common.util.NcpObjectStorageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VodServiceImpl implements VodService {

    private final VodMapper vodMapper;
    private final NcpObjectStorageUtil ncpObjectStorageUtil;

    @Override
    public void saveVodFile(Long broadcastNo, MultipartFile file, Integer duration, Long userNo) throws Exception {
        if (file.isEmpty()) throw new IllegalArgumentException("빈 파일입니다.");

        // 파일 저장 - uploads/vod 하위에 저장
        String path = ncpObjectStorageUtil.save(file, "uploads/lawyers/" + userNo + "/vod", null);
        if (path == null || path.isBlank()) {
            throw new RuntimeException("파일 저장 실패");
        }

        // DB 저장
        BroadcastVodVo vod = BroadcastVodVo.builder()
                .broadcastNo(broadcastNo)
                .vodPath(path)       // → 정적 접근 경로 저장
                .duration(duration)  // 프론트에서 전달받은 영상 길이
                .status(0)           // 기본값
                .build();

        vodMapper.insertVod(vod);
    }
}
