package com.rohithdiddi.notificationservice;

import com.rohithdiddi.notificationservice.kafka.NotificationEventListener;
import com.rohithdiddi.notificationservice.model.Events.InventoryFailedEvent;
import com.rohithdiddi.notificationservice.model.Events.InventoryReservedEvent;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class NotificationEventListenerTest {

    private final NotificationEventListener listener = new NotificationEventListener();

    @Test
    void handlesInventoryReservedWithoutError() {
        InventoryReservedEvent event = new InventoryReservedEvent(UUID.randomUUID(), "SKU-1", 2);
        assertDoesNotThrow(() -> listener.handleInventoryReserved(event));
    }

    @Test
    void handlesInventoryFailedWithoutError() {
        InventoryFailedEvent event = new InventoryFailedEvent(UUID.randomUUID(), "SKU-1", 2, "Insufficient stock");
        assertDoesNotThrow(() -> listener.handleInventoryFailed(event));
    }
}
