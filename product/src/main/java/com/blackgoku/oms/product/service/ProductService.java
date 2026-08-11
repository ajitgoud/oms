package com.blackgoku.oms.product.service;

import com.blackgoku.oms.product.dto.request.CreateProductRequest;
import com.blackgoku.oms.product.dto.response.ProductSnapshot;
import com.blackgoku.oms.product.dto.request.UpdateProductRequest;
import com.blackgoku.oms.product.dto.response.ProductResponse;
import org.springframework.data.domain.Page;

public interface ProductService {
    ProductResponse create(CreateProductRequest request);
    ProductResponse getById(Long id);
    ProductResponse update(Long id, UpdateProductRequest request);
    Page<ProductResponse> getAll(
            int page,
            int size,
            String sort,
            Boolean active
    );
    void delete(Long id);
    boolean existsById(Long id);
    ProductSnapshot getSnapshot(Long id);
}