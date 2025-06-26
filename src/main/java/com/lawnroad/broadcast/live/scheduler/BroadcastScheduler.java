package com.lawnroad.broadcast.live.scheduler;

import com.lawnroad.broadcast.live.service.BroadcastService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BroadcastScheduler {

    private final BroadcastService broadcastService;

    @Scheduled(fixedRate = 60000)
    public void checkExpiredBroadcasts() {
        broadcastService.expireOverdueBroadcasts();
    }
}
