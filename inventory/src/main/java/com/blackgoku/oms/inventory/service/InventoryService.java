package com.blackgoku.oms.inventory.service;

import com.blackgoku.oms.inventory.dto.request.*;
import com.blackgoku.oms.inventory.dto.response.InventoryResponse;
import com.blackgoku.oms.inventory.dto.response.TransactionResponse;
import org.springframework.data.domain.Page;

public interface InventoryService {

    InventoryResponse create(CreateInventoryRequest request);

    InventoryResponse getByProductId(Long productId);

    InventoryResponse reserve(Long productId, ReserveStockRequest request);

    InventoryResponse adjust(Long productId, AdjustInventoryRequest request);

    InventoryResponse release(Long productId, ReleaseStockRequest request);

    InventoryResponse consume(Long productId, ConsumeStockRequest request);

    Page<TransactionResponse> getTransactions(Long productId, int page, int size);

    /*
     * These methods will be useful when Order Service
     * starts interacting with Inventory Service.
     */
    void reserve(Long productId, Long quantity, Long orderId);

    void release(Long productId, Long quantity, Long orderId);
}