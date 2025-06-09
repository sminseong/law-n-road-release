package com.lawnroad.account.mapper;




import com.lawnroad.account.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    void insertUser(UserEntity user);

}
