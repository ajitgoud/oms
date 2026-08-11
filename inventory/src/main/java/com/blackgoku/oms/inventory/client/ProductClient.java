package com.blackgoku.oms.inventory.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class ProductClient {

    private final RestClient restClient;

    public ProductClient(
            @Value("${services.product.base-url}") String productBaseUrl
    ) {

        this.restClient = RestClient.builder()
                .baseUrl(productBaseUrl)
                .build();
    }

    public boolean exists(Long productId) {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            throw new IllegalStateException("No HTTP request context available");
        }

        String authorization = attributes.getRequest()
                        .getHeader(HttpHeaders.AUTHORIZATION);

        try {

            restClient.get()
                    .uri("/api/v1/products/{id}", productId)
                    .header(HttpHeaders.AUTHORIZATION, authorization)
                    .retrieve()
                    .toBodilessEntity();

            return true;

        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode()
                    .equals(HttpStatus.NOT_FOUND)) {

                return false;
            }

            throw new IllegalStateException(
                    "Product Service request failed",
                    ex
            );
        }
    }
}