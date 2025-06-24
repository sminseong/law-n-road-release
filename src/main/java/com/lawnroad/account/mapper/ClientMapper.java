package com.lawnroad.account.mapper;


import com.lawnroad.account.dto.ClientProfileDTO;
import com.lawnroad.account.entity.ClientEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ClientMapper {

    @Select("SELECT client_id FROM client WHERE name = #{fullName} AND email = #{email}")
    String findClientId(@Param("fullName") String fullName, @Param("email") String email);

//    @Update("UPDATE client SET pw_hash = #{pwHash} WHERE email = #{email}")
//    int updatePasswordByEmail(@Param("email") String email, @Param("pwHash") String pwHash);

    @Update("UPDATE client SET pw_hash = #{newHashedPassword} WHERE client_id = #{clientId}")
    void updatePassword(@Param("clientId") String clientId, @Param("newHashedPassword") String newHashedPassword);


    int countByClientId1(@Param("clientId") String clientId);

    int countByClientId2(@Param("clientId") String clientId);

    int countByClientNickName(@Param("nickname") String nickname);

    void insertClient(ClientEntity client);

    ClientEntity findByEmail(String email);

    ClientEntity findByClientId(String clientId);

    int countByEmail(@Param("email") String email);

    //void updateNicknameByClientId(@Param("clientId") String clientId, @Param("nickname") String nickname);

    void updateClientProfile(@Param("clientId") String clientId,
                             @Param("nickname") String nickname,
                             @Param("email") String email,
                             @Param("phone") String phone);


    void updateWithdrawalAtNow(String clientId);

    ClientProfileDTO selectProfileByClientId(@Param("clientId") String clientId);

    ClientEntity findBySocialId(String socialId);

    void insertClientBySocial(ClientEntity client);




}
