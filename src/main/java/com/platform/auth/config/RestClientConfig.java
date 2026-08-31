package com.platform.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${app.services.address-service-url:http://localhost:8082}")
    private String addressServiceUrl;

    @Value("${app.services.timeout-ms:3000}")
    private int timeoutMs;

    @Bean
    public RestClient addressServiceRestClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeoutMs);
        requestFactory.setReadTimeout(timeoutMs);

        return RestClient.builder()
                .baseUrl(addressServiceUrl)
                .requestFactory(requestFactory)
                .build();
    }
}
