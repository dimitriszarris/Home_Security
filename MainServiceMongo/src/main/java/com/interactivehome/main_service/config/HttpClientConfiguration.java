package com.interactivehome.main_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

public class HttpClientConfiguration {

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
