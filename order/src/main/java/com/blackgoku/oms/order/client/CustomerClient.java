package com.blackgoku.oms.order.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class CustomerClient {

    private final RestClient restClient;

    public CustomerClient(@Value("${services.customer.base-url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public void validateCustomer(Long customerId) {

        try {

            restClient.get()
                    .uri("/api/v1/customers/{id}", customerId)
                    .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeader())
                    .retrieve()
                    .toBodilessEntity();

        } catch (RestClientResponseException ex) {

            if (ex.getStatusCode().value() == 404) {
                throw new IllegalArgumentException("Customer not found: " + customerId);
            }

            throw new IllegalStateException("Customer Service request failed", ex);
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