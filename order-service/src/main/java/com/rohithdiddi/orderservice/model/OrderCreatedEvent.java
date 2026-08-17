package com.rohithdiddi.orderservice.model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Event published to Kafka topic "order-created" whenever a new order is placed.
 * Consumed by inventory-service to reserve stock.
 */
public record OrderCreatedEvent(
        UUID orderId,
        String productSku,
        Integer quantity,
        BigDecimal totalPrice,
        String customerId
) {
    public static OrderCreatedEvent from(Order order) {
        return new OrderCreatedEvent(
                order.getId(),
                order.getProductSku(),
                order.getQuantity(),
                order.getTotalPrice(),
                order.getCustomerId()
        );
    }
}
