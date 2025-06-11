package com.lawnroad.account.mapper;


import com.lawnroad.account.entity.ClientEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ClientMapper {

    int countByClientId(@Param("clientId") String clientId);

    int countByClientNickName(@Param("nickname") String nickname);

    void insertClient(ClientEntity client);

    ClientEntity findByEmail(String email);

    int countByEmail(@Param("email") String email);






}
