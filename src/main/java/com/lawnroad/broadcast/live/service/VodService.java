package com.lawnroad.broadcast.live.service;

import org.springframework.web.multipart.MultipartFile;

public interface VodService {
    void saveVodFile(Long broadcastNo, MultipartFile file, Integer duration) throws Exception;
}
