package com.blackgoku.oms.order.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class InventoryClient {

    private final RestClient restClient;

    public InventoryClient(@Value("${services.inventory.base-url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public void reserve(Long productId, Long quantity, Long orderId) {

        try {

            restClient.post().uri("/api/v1/inventory/{productId}/reserve", productId).header(HttpHeaders.AUTHORIZATION, getAuthorizationHeader()).body(new QuantityRequest(quantity)).retrieve().toBodilessEntity();

        } catch (RestClientResponseException ex) {

            if (ex.getStatusCode().value() == 409) {
                throw new IllegalStateException("Unable to reserve inventory for product: " + productId);
            }

            if (ex.getStatusCode().value() == 404) {
                throw new IllegalArgumentException("Inventory not found for product: " + productId);
            }

            throw new IllegalStateException("Inventory Service request failed", ex);
        }
    }

    public void release(Long productId, Long quantity, Long orderId) {

        try {

            restClient.post()
                    .uri("/api/v1/inventory/{productId}/release", productId)
                    .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeader())
                    .body(new QuantityRequest(quantity))
                    .retrieve()
                    .toBodilessEntity();

        } catch (RestClientResponseException ex) {

            throw new IllegalStateException("Failed to release inventory for product: " + productId, ex);
        }
    }

    private String getAuthorizationHeader() {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            throw new IllegalStateException("No HTTP request context available");
        }

        return attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
    }

    private record QuantityRequest(Long quantity) {
    }
}