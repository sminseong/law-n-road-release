package com.lawnroad.broadcast.live.mapper;

import com.lawnroad.broadcast.live.dto.VodListDto;
import com.lawnroad.broadcast.live.model.BroadcastVodVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VodMapper {
    void insertVod(BroadcastVodVo vod);
    List<VodListDto> findVodListPaged(@Param("offset") int offset, @Param("size") int size);
    void increaseViewCount(Long vodNo);
}
