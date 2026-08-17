package com.rohithdiddi.inventoryservice.kafka;

import com.rohithdiddi.inventoryservice.model.Events.InventoryFailedEvent;
import com.rohithdiddi.inventoryservice.model.Events.InventoryReservedEvent;
import com.rohithdiddi.inventoryservice.model.Events.OrderCreatedEvent;
import com.rohithdiddi.inventoryservice.model.InventoryItem;
import com.rohithdiddi.inventoryservice.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventListener {

    private static final Logger log = LoggerFactory.getLogger(InventoryEventListener.class);

    private final InventoryRepository inventoryRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public InventoryEventListener(InventoryRepository inventoryRepository,
                                   KafkaTemplate<String, Object> kafkaTemplate) {
        this.inventoryRepository = inventoryRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "order-created", groupId = "inventory-service")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent for orderId={} sku={}", event.orderId(), event.productSku());

        InventoryItem item = inventoryRepository.findByProductSku(event.productSku())
                .orElseGet(() -> {
                    log.warn("No inventory record found for sku={}, treating as 0 stock", event.productSku());
                    return new InventoryItem(event.productSku(), 0);
                });

        if (item.getAvailableQuantity() >= event.quantity()) {
            item.setAvailableQuantity(item.getAvailableQuantity() - event.quantity());
            inventoryRepository.save(item);

            InventoryReservedEvent reserved = new InventoryReservedEvent(
                    event.orderId(), event.productSku(), event.quantity());
            kafkaTemplate.send("inventory-reserved", event.orderId().toString(), reserved);
            log.info("Reserved {} units of sku={} for orderId={}", event.quantity(), event.productSku(), event.orderId());
        } else {
            InventoryFailedEvent failed = new InventoryFailedEvent(
                    event.orderId(), event.productSku(), event.quantity(), "Insufficient stock");
            kafkaTemplate.send("inventory-failed", event.orderId().toString(), failed);
            log.warn("Insufficient stock for sku={} orderId={} (available={}, requested={})",
                    event.productSku(), event.orderId(), item.getAvailableQuantity(), event.quantity());
        }
    }
}
