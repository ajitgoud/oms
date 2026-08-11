package com.blackgoku.oms.inventory.controller;

import com.blackgoku.oms.inventory.dto.request.*;
import com.blackgoku.oms.inventory.dto.response.InventoryResponse;
import com.blackgoku.oms.inventory.dto.response.TransactionResponse;
import com.blackgoku.oms.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryResponse> create(@Valid @RequestBody CreateInventoryRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(inventoryService.create(request));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> getByProductId(@PathVariable Long productId) {

        return ResponseEntity.ok(
                inventoryService.getByProductId(productId)
        );
    }

    @PostMapping("/{productId}/reserve")
    public ResponseEntity<InventoryResponse> reserve(
            @PathVariable Long productId,
            @Valid @RequestBody
            ReserveStockRequest request
    ) {

        return ResponseEntity.ok(
                inventoryService.reserve(productId, request)
        );
    }

    @PostMapping("/{productId}/adjust")
    public ResponseEntity<InventoryResponse> adjust(
            @PathVariable Long productId,
            @Valid @RequestBody
            AdjustInventoryRequest request
    ) {

        return ResponseEntity.ok(
                inventoryService.adjust(productId, request)
        );
    }

    @PostMapping("/{productId}/release")
    public ResponseEntity<InventoryResponse> release(
            @PathVariable Long productId,

            @Valid
            @RequestBody
            ReleaseStockRequest request
    ) {

        return ResponseEntity.ok(
                inventoryService.release(productId, request)
        );
    }

    @PostMapping("/{productId}/consume")
    public ResponseEntity<InventoryResponse> consume(
            @PathVariable Long productId,
            @Valid @RequestBody ConsumeStockRequest request
    ) {
        return ResponseEntity.ok(
                inventoryService.consume(productId, request)
        );
    }

    @GetMapping("/{productId}/transactions")
    public ResponseEntity<Page<TransactionResponse>>
    getTransactions(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "20")
            int size
    ) {

        return ResponseEntity.ok(
                inventoryService.getTransactions(productId, page, size)
        );
    }
}