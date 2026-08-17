package com.rohithdiddi.inventoryservice.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Events {

    public record OrderCreatedEvent(
            UUID orderId,
            String productSku,
            Integer quantity,
            BigDecimal totalPrice,
            String customerId
    ) {
    }

    public record InventoryReservedEvent(
            UUID orderId,
            String productSku,
            Integer quantity
    ) {
    }

    public record InventoryFailedEvent(
            UUID orderId,
            String productSku,
            Integer quantity,
            String reason
    ) {
    }
}
