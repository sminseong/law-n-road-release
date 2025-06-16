package com.lawnroad.broadcast.live.mapper;

import com.lawnroad.broadcast.live.dto.BroadcastSessionDto;
import com.lawnroad.broadcast.live.dto.BroadcastViewDetailDto;
import com.lawnroad.broadcast.live.model.BroadcastVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BroadcastMapper {
    void insertBroadcast(BroadcastVo vo);
    BroadcastVo findByScheduleNo(Long scheduleNo);
    BroadcastSessionDto findByNo(Long broadcastNo);
    BroadcastVo findBySessionId(@Param("sessionId") String sessionId);

    BroadcastViewDetailDto findDetailByScheduleNo(@Param("scheduleNo") Long scheduleNo);
    BroadcastViewDetailDto findDetailByBroadcastNo(@Param("broadcastNo") Long broadcastNo);
    List<String> findKeywordsByScheduleNo(@Param("scheduleNo") Long scheduleNo);
    Long findScheduleNoByBroadcastNo(@Param("broadcastNo") Long broadcastNo);

    void endBroadcast(@Param("broadcastNo") Long broadcastNo);
}
