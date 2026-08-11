package com.blackgoku.oms.customer.repository;

import com.blackgoku.oms.customer.entity.Customer;
import com.blackgoku.oms.customer.entity.CustomerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository
        extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByCustomerCode(String customerCode);

    Page<Customer> findAllByStatus(
            CustomerStatus status,
            Pageable pageable
    );
}