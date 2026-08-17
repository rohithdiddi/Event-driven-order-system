package com.rohithdiddi.inventoryservice;

import com.rohithdiddi.inventoryservice.kafka.InventoryEventListener;
import com.rohithdiddi.inventoryservice.model.Events.OrderCreatedEvent;
import com.rohithdiddi.inventoryservice.model.InventoryItem;
import com.rohithdiddi.inventoryservice.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class InventoryEventListenerTest {

    private InventoryRepository inventoryRepository;
    private KafkaTemplate<String, Object> kafkaTemplate;
    private InventoryEventListener listener;

    @BeforeEach
    void setUp() {
        inventoryRepository = mock(InventoryRepository.class);
        kafkaTemplate = mock(KafkaTemplate.class);
        listener = new InventoryEventListener(inventoryRepository, kafkaTemplate);
    }

    @Test
    void reservesStockWhenAvailable() {
        InventoryItem item = new InventoryItem("SKU-1", 10);
        when(inventoryRepository.findByProductSku("SKU-1")).thenReturn(Optional.of(item));

        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(), "SKU-1", 3, new BigDecimal("29.99"), "customer-1");

        listener.handleOrderCreated(event);

        assertEquals(7, item.getAvailableQuantity());
        verify(inventoryRepository, times(1)).save(item);
        verify(kafkaTemplate, times(1)).send(eq("inventory-reserved"), any(), any());
        verify(kafkaTemplate, never()).send(eq("inventory-failed"), any(), any());
    }

    @Test
    void publishesFailureWhenInsufficientStock() {
        InventoryItem item = new InventoryItem("SKU-2", 1);
        when(inventoryRepository.findByProductSku("SKU-2")).thenReturn(Optional.of(item));

        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(), "SKU-2", 5, new BigDecimal("15.00"), "customer-2");

        listener.handleOrderCreated(event);

        verify(inventoryRepository, never()).save(any());
        verify(kafkaTemplate, times(1)).send(eq("inventory-failed"), any(), any());
        verify(kafkaTemplate, never()).send(eq("inventory-reserved"), any(), any());
    }
}
