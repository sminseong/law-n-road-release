package com.lawnroad.account.mapper;

import com.lawnroad.account.entity.AdminEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper {

    AdminEntity findByAdminId(String adminId);


}
