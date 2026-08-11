package com.blackgoku.oms.customer.service;

import com.blackgoku.oms.customer.dto.request.CreateCustomerRequest;
import com.blackgoku.oms.customer.dto.request.UpdateCustomerRequest;
import com.blackgoku.oms.customer.dto.response.CustomerResponse;
import org.springframework.data.domain.Page;

public interface CustomerService {

    CustomerResponse create(CreateCustomerRequest request);

    CustomerResponse getById(Long id);

    CustomerResponse update(
            Long id,
            UpdateCustomerRequest request
    );

    Page<CustomerResponse> getCustomers(
            int page,
            int size
    );

    void validateCustomer(Long customerId);
}