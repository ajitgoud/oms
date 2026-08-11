package com.blackgoku.oms.inventory.service.impl;

import com.blackgoku.oms.inventory.client.ProductClient;
import com.blackgoku.oms.inventory.dto.request.*;
import com.blackgoku.oms.inventory.dto.response.InventoryResponse;
import com.blackgoku.oms.inventory.dto.response.TransactionResponse;
import com.blackgoku.oms.inventory.entity.Inventory;
import com.blackgoku.oms.inventory.entity.InventoryReferenceType;
import com.blackgoku.oms.inventory.entity.InventoryTransaction;
import com.blackgoku.oms.inventory.entity.InventoryTransactionType;
import com.blackgoku.oms.inventory.exception.*;
import com.blackgoku.oms.inventory.repository.InventoryRepository;
import com.blackgoku.oms.inventory.repository.InventoryTransactionRepository;
import com.blackgoku.oms.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final ProductClient productClient;

    @Transactional
    @Override
    public InventoryResponse create(CreateInventoryRequest request) {
        if (!productClient.exists(request.productId())) {
            throw new IllegalArgumentException("Product not found: " + request.productId());
        }

        if (inventoryRepository.existsByProductId(request.productId())) {
            throw new InventoryAlreadyExistsException("Inventory already exists for product: " + request.productId());
        }

        Inventory inventory = Inventory.builder()
                .productId(request.productId())
                .totalQuantity(request.totalQuantity())
                .reservedQuantity(0L)
                .build();

        Inventory savedInventory = inventoryRepository.save(inventory);

        InventoryTransaction transaction = InventoryTransaction.builder()
                .inventoryId(savedInventory.getId())
                .type(InventoryTransactionType.INITIAL_STOCK)
                .quantity(request.totalQuantity())
                .referenceId(generateSystemReferenceId())
                .referenceType(InventoryReferenceType.ORDER)
                .reason("Initial inventory").build();
        transactionRepository.save(transaction);

        return toResponse(savedInventory);
    }

    @Transactional(readOnly = true)
    @Override
    public InventoryResponse getByProductId(Long productId) {
        Inventory inventory = findInventory(productId);
        return toResponse(inventory);
    }

    @Transactional
    @Override
    public InventoryResponse reserve(Long productId, ReserveStockRequest request) {

        Inventory inventory = findInventoryWithLock(productId);

        if (inventory.getAvailableQuantity() < request.quantity()) {
            throw new InsufficientStockException("Insufficient stock");
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() + request.quantity());
        saveReservationTransaction(inventory, request.quantity(), null, "Stock reserved");
        return toResponse(inventory);
    }

    @Transactional
    @Override
    public void reserve(Long productId, Long quantity, Long orderId) {

        Inventory inventory = findInventoryWithLock(productId);
        if (inventory.getAvailableQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock");
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
        saveReservationTransaction(inventory, quantity, orderId, "Order inventory reservation");
    }

    @Transactional
    @Override
    public InventoryResponse adjust(Long productId, AdjustInventoryRequest request) {

        Inventory inventory = findInventoryWithLock(productId);

        long newTotal = inventory.getTotalQuantity() + request.quantity();
        if (newTotal < inventory.getReservedQuantity()) {
            throw new InvalidStockAdjustmentException("Stock adjustment would make available stock negative " + "for product: " + productId);
        }

        inventory.setTotalQuantity(newTotal);

        InventoryTransaction transaction = InventoryTransaction.builder()
                .inventoryId(inventory.getId())
                .type(InventoryTransactionType.STOCK_ADJUSTMENT)
                .quantity(request.quantity())
                .referenceId(generateSystemReferenceId())
                .referenceType(InventoryReferenceType.ORDER)
                .reason(request.reason())
                .build();
        transactionRepository.save(transaction);

        return toResponse(inventory);
    }

    @Transactional
    @Override
    public InventoryResponse release(Long productId, ReleaseStockRequest request) {

        Inventory inventory = findInventoryWithLock(productId);
        if (inventory.getReservedQuantity() < request.quantity()) {
            throw new InvalidReservationReleaseException("Invalid reservation release");
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() - request.quantity());
        saveReleaseTransaction(inventory, request.quantity(), null, "Reservation released");
        return toResponse(inventory);
    }

    @Transactional
    @Override
    public void release(Long productId, Long quantity, Long orderId) {

        Inventory inventory = findInventoryWithLock(productId);
        if (inventory.getReservedQuantity() < quantity) {
            throw new InvalidReservationReleaseException("Invalid reservation release");
        }
        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        saveReleaseTransaction(inventory, quantity, orderId, "Order cancelled");
    }

    @Transactional
    @Override
    public InventoryResponse consume(Long productId, ConsumeStockRequest request) {

        Inventory inventory = findInventoryWithLock(productId);

        if (inventory.getReservedQuantity() < request.quantity()) {
            throw new InvalidStockConsumptionException("Cannot consume more reserved stock than exists");
        }

        inventory.setTotalQuantity(inventory.getTotalQuantity() - request.quantity());
        inventory.setReservedQuantity(inventory.getReservedQuantity() - request.quantity());
        InventoryTransaction transaction = InventoryTransaction.builder()
                .inventoryId(inventory.getId())
                .type(InventoryTransactionType.STOCK_CONSUMED)
                .quantity(request.quantity())
                .referenceId(generateSystemReferenceId())
                .referenceType(InventoryReferenceType.ORDER)
                .reason("Reserved stock consumed")
                .build();
        transactionRepository.save(transaction);

        return toResponse(inventory);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<TransactionResponse> getTransactions(Long productId, int page, int size) {

        if (page < 0) {
            throw new IllegalArgumentException("Page must be >= 0");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Size must be between 1 and 100");
        }

        Inventory inventory = findInventory(productId);
        Pageable pageable = PageRequest.of(page, size);

        return transactionRepository.findByInventoryIdOrderByCreatedAtDesc(inventory.getId(), pageable).map(this::toResponse);
    }

    private Inventory findInventory(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for product: " + productId));
    }

    private Inventory findInventoryWithLock(Long productId) {
        return inventoryRepository.findWithLockByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for product: " + productId));
    }

    private void saveReservationTransaction(Inventory inventory, Long quantity, Long orderId, String reason) {
        InventoryTransaction transaction = InventoryTransaction.builder()
                .inventoryId(inventory.getId())
                .type(InventoryTransactionType.RESERVATION)
                .quantity(quantity)
                .referenceId(orderId != null ? orderId : generateSystemReferenceId())
                .referenceType(InventoryReferenceType.ORDER)
                .reason(reason)
                .build();

        transactionRepository.save(transaction);
    }

    private void saveReleaseTransaction(Inventory inventory, Long quantity, Long orderId, String reason) {
        InventoryTransaction transaction = InventoryTransaction.builder()
                .inventoryId(inventory.getId())
                .type(InventoryTransactionType.RESERVATION_RELEASE)
                .quantity(quantity)
                .referenceId(orderId != null ? orderId : generateSystemReferenceId())
                .referenceType(InventoryReferenceType.ORDER)
                .reason(reason)
                .build();
        transactionRepository.save(transaction);
    }

    private InventoryResponse toResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(inventory.getProductId())
                .totalQuantity(inventory.getTotalQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .availableQuantity(inventory.getAvailableQuantity())
                .version(inventory.getVersion())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }

    private TransactionResponse toResponse(InventoryTransaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .inventoryId(transaction.getInventoryId())
                .type(transaction.getType())
                .quantity(transaction.getQuantity())
                .referenceId(transaction.getReferenceId())
                .referenceType(transaction.getReferenceType())
                .reason(transaction.getReason())
                .createdAt(transaction.getCreatedAt())
                .createdBy(transaction.getCreatedBy())
                .build();
    }

    private Long generateSystemReferenceId() {
        return Math.abs(UUID.randomUUID().getMostSignificantBits());
    }
}