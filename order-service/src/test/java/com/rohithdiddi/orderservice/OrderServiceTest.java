package com.rohithdiddi.orderservice;

import com.rohithdiddi.orderservice.kafka.OrderEventPublisher;
import com.rohithdiddi.orderservice.model.CreateOrderRequest;
import com.rohithdiddi.orderservice.model.Order;
import com.rohithdiddi.orderservice.repository.OrderRepository;
import com.rohithdiddi.orderservice.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderRepository orderRepository;
    private OrderEventPublisher eventPublisher;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        eventPublisher = mock(OrderEventPublisher.class);
        orderService = new OrderService(orderRepository, eventPublisher);
    }

    @Test
    void createOrder_savesOrderAndPublishesEvent() {
        CreateOrderRequest request = new CreateOrderRequest("SKU-123", 2, new BigDecimal("49.99"), "customer-1");

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.createOrder(request);

        assertNotNull(result);
        assertEquals("SKU-123", result.getProductSku());
        assertEquals(2, result.getQuantity());

        verify(orderRepository, times(1)).save(any(Order.class));

        ArgumentCaptor<com.rohithdiddi.orderservice.model.OrderCreatedEvent> eventCaptor =
                ArgumentCaptor.forClass(com.rohithdiddi.orderservice.model.OrderCreatedEvent.class);
        verify(eventPublisher, times(1)).publish(eventCaptor.capture());
        assertEquals("SKU-123", eventCaptor.getValue().productSku());
    }

    @Test
    void getOrder_throwsWhenNotFound() {
        UUID randomId = UUID.randomUUID();
        when(orderRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(java.util.NoSuchElementException.class, () -> orderService.getOrder(randomId));
    }
}
