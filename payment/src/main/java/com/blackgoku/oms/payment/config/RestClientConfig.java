package com.blackgoku.oms.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient orderRestClient(
            @Value("${services.order.url}") String orderServiceUrl
    ) {
        return RestClient.builder()
                .baseUrl(orderServiceUrl)
                .build();
    }
}