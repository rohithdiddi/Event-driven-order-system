package com.rohithdiddi.notificationservice.model;

import java.util.UUID;

public class Events {

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
