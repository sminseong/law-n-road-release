package com.lawnroad.broadcast.live.mapper;

import com.lawnroad.broadcast.live.dto.BroadcastSessionDto;
import com.lawnroad.broadcast.live.model.BroadcastVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BroadcastMapper {
    void insertBroadcast(BroadcastVo vo);
    BroadcastVo findByScheduleNo(Long scheduleNo);
    BroadcastSessionDto findByNo(Long broadcastNo);
    BroadcastVo findBySessionId(@Param("sessionId") String sessionId);
}
