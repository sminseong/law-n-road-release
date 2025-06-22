package com.lawnroad.broadcast.live.mapper;

import com.lawnroad.broadcast.live.model.BroadcastVodVo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VodMapper {
    void insertVod(BroadcastVodVo vod);
}
