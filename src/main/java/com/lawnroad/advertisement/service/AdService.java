package com.lawnroad.advertisement.service;

import com.lawnroad.advertisement.dto.AdRegisterDto;
import com.lawnroad.advertisement.dto.AdvertisementDetailResponseDto;
import com.lawnroad.advertisement.dto.AdvertisementListResponseDto;
import com.lawnroad.advertisement.dto.PagedAdvertisementResponseDto;
import com.lawnroad.advertisement.mapper.AdMapper;
import com.lawnroad.common.util.NcpObjectStorageUtil;
import com.lawnroad.payment.dto.OrdersCreateDTO;
import com.lawnroad.payment.service.OrdersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdService {
  
  private final AdMapper advertisementMapper;
  private final OrdersService ordersService;
  private final NcpObjectStorageUtil ncpObjectStorageUtil;
  
  public PagedAdvertisementResponseDto getAdsByUserWithPaging(Long userNo, int page, int limit) {
    // 페이징 계산
    int offset = (page - 1) * limit;
    
    // 데이터 조회
    List<AdvertisementListResponseDto> ads = advertisementMapper.selectAdsByUserWithPaging(userNo, offset, limit);
    
    // 전체 개수 조회
    int totalCount = advertisementMapper.countAdsByUser(userNo);
    
    // 전체 페이지 수 계산
    int totalPages = (int) Math.ceil((double) totalCount / limit);
    
    // DTO로 반환
    return new PagedAdvertisementResponseDto(totalCount, totalPages, ads);
  }
  
  public AdvertisementDetailResponseDto getAdDetailByAdNo(Long adNo) {
    return advertisementMapper.selectAdDetailByAdNo(adNo);
  }
  
  // 광고 등록
  @Transactional
  public Long createAdOrderAndRegister(AdRegisterDto dto) {
    int amount = "MAIN".equalsIgnoreCase(dto.getAdType()) ? 400_000 : 150_000;
    
    OrdersCreateDTO orderDto = new OrdersCreateDTO();
    orderDto.setUserNo(dto.getUserNo());
    orderDto.setOrderCode("ADS-" +
            UUID.randomUUID().toString().replace("-", "").substring(0, 16)
    );
    orderDto.setAmount((long) amount);
    orderDto.setStatus("ORDERED");
    orderDto.setOrderType("ADVERTISEMENT");
    
    Long orderNo = ordersService.createOrder(orderDto);
    dto.setOrdersNo(orderNo);
    
    String imagePath = ncpObjectStorageUtil.save(
        dto.getFile(),
        "uploads/lawyers/" + dto.getUserNo() + "/ads",
        null
    );
    
    dto.setAdPath(imagePath);
    dto.setAdStatus(0);
    dto.setApprovalStatus("PENDING");
    
    advertisementMapper.insertAd(dto);
    
    return orderNo;
  }
  
  // 광고 삭제
  public void deleteAd(Long adNo) {
    advertisementMapper.deleteAd(adNo);
  }
}
