package com.lawnroad.broadcast.chat.mapper;


import com.lawnroad.broadcast.chat.dto.AutoReplyDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AutoReplyMapper {
    void insertAutoReply(AutoReplyDTO autoReplyDTO);
   List<AutoReplyDTO> findByAutoReply(Long scheduleNo);
    void deleteAutoReply(Long no);

}
