package com.lawnroad.broadcast.live.mapper;

import com.lawnroad.broadcast.live.dto.VodDetailDto;
import com.lawnroad.broadcast.live.dto.VodListDto;
import com.lawnroad.broadcast.live.dto.VodListItemDto;
import com.lawnroad.broadcast.live.dto.VodPreviewDto;
import com.lawnroad.broadcast.live.model.BroadcastVodVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VodMapper {
    void insertVod(BroadcastVodVo vod);
    List<VodListDto> findVodListPaged(@Param("offset") int offset,
                                      @Param("size") int size,
                                      @Param("sort") String sort,
                                      @Param("categoryNo") Long categoryNo);
    void increaseViewCount(Long vodNo);
    // vod 상세
    VodDetailDto findVodDetailByBroadcastNo(Long broadcastNo);
    // 페이지네이션 관련
    long countVodByCondition(@Param("categoryNo") Long categoryNo);



    String selectNameByUserNo(Long userNo);
    // 홈에 띄울 vod
    List<VodListItemDto> findAllVods();
    // 마이페이지에 띄울 vod
    List<VodPreviewDto> findVodListByLawyer(@Param("lawyerNo") Long lawyerNo,
                                            @Param("offset") int offset,
                                            @Param("limit") int limit,
                                            @Param("sort") String sort);
    int countVodByLawyer(@Param("lawyerNo") Long lawyerNo);
}
