package com.blackgoku.oms.order.client;

import com.blackgoku.oms.order.dto.response.ProductSnapshot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class ProductClient {

    private final RestClient restClient;

    public ProductClient(@Value("${services.product.base-url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public ProductSnapshot getSnapshot(Long productId) {

        try {

            return restClient.get()
                    .uri("/api/v1/products/{id}", productId)
                    .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeader())
                    .retrieve()
                    .body(ProductSnapshot.class);

        } catch (RestClientResponseException ex) {

            if (ex.getStatusCode().value() == 404) {
                throw new IllegalArgumentException("Product not found: " + productId);
            }

            throw new IllegalStateException("Product Service request failed", ex);
        }
    }

    private String getAuthorizationHeader() {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            throw new IllegalStateException("No HTTP request context available");
        }

        return attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
    }
}