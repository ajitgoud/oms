package com.blackgoku.oms.product.controller;

import com.blackgoku.oms.product.dto.request.CreateProductRequest;
import com.blackgoku.oms.product.dto.request.ProductSnapshot;
import com.blackgoku.oms.product.dto.request.UpdateProductRequest;
import com.blackgoku.oms.product.dto.response.ProductResponse;
import com.blackgoku.oms.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping

    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                productService.getById(id)
        );
    }

    @GetMapping

    public ResponseEntity<Page<ProductResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id,asc") String sort,
            @RequestParam(required = false) Boolean active
    ) {
        return ResponseEntity.ok(productService.getAll(page, size, sort, active)
        );

    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /*
     * Internal service-to-service representation.
     *
     * Order Service will eventually use this when it needs
     * the current product price/active state.
     */
    @GetMapping("/{id}/snapshot")
    public ResponseEntity<ProductSnapshot> getSnapshot(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getSnapshot(id));
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> exists(@PathVariable Long id) {
        return ResponseEntity.ok(productService.existsById(id));
    }

}
