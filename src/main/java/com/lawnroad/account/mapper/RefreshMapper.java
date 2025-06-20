package com.lawnroad.account.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RefreshMapper {

    void save(@Param("userNo") Long userNo, @Param("token") String token);


    void deleteByUserNo(Long userNo);
}


