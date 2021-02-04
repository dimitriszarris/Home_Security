package com.interactivehome.main_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("iot")
@Data
public class AppProperties {
  private String securityControllerIpPort;
  private String verificationProcessEndpoint;
  private Integer verificationProcessTimeoutSec;
  private String alarmStateEndpoint;
  private String stopAlarmEndpoint;
}
