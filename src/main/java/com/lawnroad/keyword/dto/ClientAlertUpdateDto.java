package com.lawnroad.keyword.dto;

import lombok.Data;

@Data
public class ClientAlertUpdateDto {
  private Long clientNo;
  private boolean isConsultAlert;
  private boolean isKeywordAlert;
  
  // Lombok이 잘못 만드는 것을 수동으로 오버라이드
  public boolean getIsConsultAlert() {
    return isConsultAlert;
  }
  
  public void setIsConsultAlert(boolean isConsultAlert) {
    this.isConsultAlert = isConsultAlert;
  }
  
  public boolean getIsKeywordAlert() {
    return isKeywordAlert;
  }
  
  public void setIsKeywordAlert(boolean isKeywordAlert) {
    this.isKeywordAlert = isKeywordAlert;
  }
}