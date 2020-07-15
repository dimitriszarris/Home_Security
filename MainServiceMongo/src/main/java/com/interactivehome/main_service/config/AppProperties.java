package com.interactivehome.main_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("verification")
@Data
public class AppProperties {
  public String securityControllerEndpoint;
  public Integer verificationProcessTimeoutSec;
}
