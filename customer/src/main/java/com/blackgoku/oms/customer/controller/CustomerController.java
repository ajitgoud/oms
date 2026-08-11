package com.blackgoku.oms.customer.controller;

import com.blackgoku.oms.customer.dto.request.CreateCustomerRequest;
import com.blackgoku.oms.customer.dto.request.UpdateCustomerRequest;
import com.blackgoku.oms.customer.dto.response.CustomerResponse;
import com.blackgoku.oms.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponse> create(
            @Valid @RequestBody CreateCustomerRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(customerService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                customerService.getById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCustomerRequest request
    ) {
        return ResponseEntity.ok(
                customerService.update(id, request)
        );
    }

    @GetMapping
    public ResponseEntity<Page<CustomerResponse>> getCustomers(
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "20")
            int size
    ) {
        return ResponseEntity.ok(
                customerService.getCustomers(page, size)
        );
    }

    /*
     * Service-to-service endpoint.
     *
     * Used when another service needs to verify that
     * a customer exists and is ACTIVE.
     */
    @GetMapping("/{id}/validate")
    public ResponseEntity<Void> validateCustomer(
            @PathVariable Long id
    ) {
        customerService.validateCustomer(id);
        return ResponseEntity.noContent().build();
    }
}