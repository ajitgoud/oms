package com.blackgoku.oms.customer.service.impl;

import com.blackgoku.oms.customer.dto.request.CreateCustomerRequest;
import com.blackgoku.oms.customer.dto.request.UpdateCustomerRequest;
import com.blackgoku.oms.customer.dto.response.CustomerResponse;
import com.blackgoku.oms.customer.entity.Customer;
import com.blackgoku.oms.customer.entity.CustomerStatus;
import com.blackgoku.oms.customer.exception.CustomerAlreadyExistsException;
import com.blackgoku.oms.customer.exception.CustomerNotFoundException;
import com.blackgoku.oms.customer.exception.CustomerVersionConflictException;
import com.blackgoku.oms.customer.repository.CustomerRepository;
import com.blackgoku.oms.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl
        implements CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    @Override
    public CustomerResponse create(
            CreateCustomerRequest request
    ) {

        if (customerRepository.existsByEmail(request.email())) {
            throw new CustomerAlreadyExistsException(
                    "Customer already exists"
            );
        }

        Customer customer = Customer.builder()
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .status(CustomerStatus.ACTIVE)
                .customerCode(generateCustomerCode())
                .build();

        Customer saved = customerRepository.save(customer);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public CustomerResponse getById(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer not found"
                        ));

        return toResponse(customer);
    }

    @Transactional
    @Override
    public CustomerResponse update(
            Long id,
            UpdateCustomerRequest request
    ) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer not found"
                        ));

        if (!customer.getVersion().equals(request.version())) {
            throw new CustomerVersionConflictException(
                    "Customer was modified by another request"
            );
        }

        customer.setName(request.name());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());

        return toResponse(customer);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CustomerResponse> getCustomers(
            int page,
            int size
    ) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page must be >= 0"
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Size must be between 1 and 100"
            );
        }

        Pageable pageable = PageRequest.of(page, size);

        return customerRepository
                .findAll(pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public void validateCustomer(Long customerId) {
        Customer customer =
                customerRepository.findById(customerId)
                        .orElseThrow(() ->
                                new CustomerNotFoundException(
                                        "Customer not found"
                                ));

        if (customer.getStatus() != CustomerStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Customer not active"
            );
        }
    }

    private CustomerResponse toResponse(
            Customer customer
    ) {

        return CustomerResponse.builder()
                .id(customer.getId())
                .customerId(customer.getCustomerCode())
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .status(customer.getStatus())
                .version(customer.getVersion())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }

    private String generateCustomerCode() {

        return "CUST-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();
    }
}