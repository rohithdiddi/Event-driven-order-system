package com.rohithdiddi.notificationservice.kafka;

import com.rohithdiddi.notificationservice.model.Events.InventoryFailedEvent;
import com.rohithdiddi.notificationservice.model.Events.InventoryReservedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * In a real system this would call an email/SMS/push provider.
 * Here we simulate that by logging a clear, structured "notification sent" message,
 * which is enough to demonstrate the event-driven flow end to end.
 */
@Component
public class NotificationEventListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventListener.class);

    @KafkaListener(topics = "inventory-reserved", groupId = "notification-service",
            containerFactory = "inventoryReservedListenerFactory")
    public void handleInventoryReserved(InventoryReservedEvent event) {
        log.info("NOTIFY customer: order {} confirmed - {} unit(s) of {} reserved.",
                event.orderId(), event.quantity(), event.productSku());
    }

    @KafkaListener(topics = "inventory-failed", groupId = "notification-service",
            containerFactory = "inventoryFailedListenerFactory")
    public void handleInventoryFailed(InventoryFailedEvent event) {
        log.info("NOTIFY customer: order {} could NOT be fulfilled - {} (sku={}, requested={}).",
                event.orderId(), event.reason(), event.productSku(), event.quantity());
    }
}
