package com.rohithdiddi.orderservice.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotBlank(message = "productSku is required") String productSku,
        @NotNull @Min(value = 1, message = "quantity must be at least 1") Integer quantity,
        @NotNull @Positive(message = "totalPrice must be positive") BigDecimal totalPrice,
        @NotBlank(message = "customerId is required") String customerId
) {
}
