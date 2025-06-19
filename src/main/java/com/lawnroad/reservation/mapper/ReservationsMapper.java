package com.lawnroad.reservation.mapper;

import com.lawnroad.reservation.dto.ReservationsCreateDTO;
import com.lawnroad.reservation.dto.ReservationsResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReservationsMapper {
    void insertReservation(ReservationsCreateDTO dto);
    List<ReservationsResponseDTO> selectReservationsByUser(@Param("userNo") Long userNo);
    void updateReservationStatus(@Param("reservationNo") Long reservationNo,
                           @Param("status") String status);
    int countByUserNoAndStatus(
            @Param("userNo") Long userNo,
            @Param("status") String status
    );
    List<ReservationsResponseDTO> selectByLawyerNo(@Param("lawyerNo") Long lawyerNo);
    ReservationsResponseDTO selectReservationByNo(@Param("reservationNo") Long reservationNo);
}
