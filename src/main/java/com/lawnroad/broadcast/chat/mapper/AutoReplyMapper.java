package com.lawnroad.broadcast.chat.mapper;


import com.lawnroad.broadcast.chat.dto.AutoReplyDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AutoReplyMapper {
    void insertAutoReply(AutoReplyDTO autoReplyDTO);
    List<AutoReplyDTO> findByAutoReply(Long scheduleNo);
    void deleteAutoReply(Long no);
    String findReplyMessage(@Param("broadcastNo") Long broadcastNo, @Param("keyword") String keyword);

}
