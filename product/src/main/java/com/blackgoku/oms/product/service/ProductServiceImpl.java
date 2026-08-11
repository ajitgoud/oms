package com.blackgoku.oms.product.service;

import com.blackgoku.oms.product.dto.request.CreateProductRequest;
import com.blackgoku.oms.product.dto.response.ProductSnapshot;
import com.blackgoku.oms.product.dto.request.UpdateProductRequest;
import com.blackgoku.oms.product.dto.response.ProductResponse;
import com.blackgoku.oms.product.entity.Product;
import com.blackgoku.oms.product.exception.ProductAlreadyExistsException;
import com.blackgoku.oms.product.exception.ProductNotFoundException;
import com.blackgoku.oms.product.exception.ProductVersionConflictException;
import com.blackgoku.oms.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "name", "price", "createdAt");
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public ProductResponse create(CreateProductRequest request) {
        if(productRepository.existsBySku(request.sku())){
            throw new ProductAlreadyExistsException("Product SKU already exists");
        }

        Product product = Product.builder()
                .sku(request.sku())
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .active(true)
                .build();

        Product savedProduct = productRepository.save(product);

        return toResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    @Override
    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new ProductNotFoundException("Product not found"));

        return toResponse(product);
    }

    @Transactional
    @Override
    public ProductResponse update(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new ProductNotFoundException("Product not found"));

        if(!product.getVersion().equals(request.version())){
            throw new ProductVersionConflictException("Product was modified by another request");
        }

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setActive(request.active());

        return toResponse(product);
    }

    @Override
    public Page<ProductResponse> getAll(int page, int size, String sort, Boolean active) {
        if(page<0){
            throw new IllegalArgumentException("Page must be >= 0");
        }

        if(size<1 || size>100){
            throw new IllegalArgumentException("Size must be between 1 and 100");
        }

        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        Page<Product> products;
        if(active == null){
            products = productRepository.findAll(pageable);
        }else{
            products = productRepository.findByActive(active, pageable);
        }

        return products.map(this::toResponse);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new ProductNotFoundException("Product not found"));
        productRepository.delete(product);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }

    @Override
    public ProductSnapshot getSnapshot(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new ProductNotFoundException("Product not found"));

        return ProductSnapshot.builder()
                .id(product.getId())
                .price(product.getPrice())
                .active(product.isActive())
                .build();
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .active(product.isActive())
                .version(product.getVersion())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();

    }

    private Sort parseSort(String sort){
        String[] parts = sort.split(",");
        String property = parts[0];
        if(!ALLOWED_SORT_FIELDS.contains(property)){
            throw new IllegalArgumentException("Invalid sort field: " + property);
        }
        Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1])?Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, property);
    }
}
