package com.lawnroad.common.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "solapi")
// application.properties 에서 설정된 커스텀 kakao 설정을 읽어들이기 위한 클래스
// application.properties 내부에 아래와 같이 설정
/*
  solapi.api-key=${SOLAPI_API_KEY}
  solapi.api-secret=${SOLAPI_API_SECRET}
 */
public class SolapiConfig {
  private String apiKey;
  private String apiSecret;
  private String apiUrl;
  private String from;
  private String pfId;

  public void setApiKey(String apiKey) { this.apiKey = apiKey; }
  public void setApiSecret(String apiSecret) { this.apiSecret = apiSecret; }
  public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }
  public void setFrom(String from) { this.from = from; }
  public void setPfId(String pfId) { this.pfId = pfId; }
}