package com.lawnroad.account.mapper;

import com.lawnroad.account.entity.ClientEntity;
import com.lawnroad.account.entity.LawyerEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface LawyerMapper {

    int countByLawyerId1(@Param("lawyerId") String lawyerId);// 변호사 테이블에서 중복여부 체크

    int countByLawyerId2(@Param("lawyerId") String lawyerId); // 의로인 테이블에서 중복여부 체크

    void insertLawyer(LawyerEntity lawyer);

    LawyerEntity findByEmail(String email);

    LawyerEntity findByClientId(String lawyerId);

    int countByEmail(@Param("email") String email);

    LawyerEntity findByLawyerId(String lawyerId);
}
