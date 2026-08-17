package com.rohithdiddi.orderservice.model;

public enum OrderStatus {
    PENDING,
    INVENTORY_RESERVED,
    INVENTORY_FAILED,
    CONFIRMED,
    CANCELLED
}
