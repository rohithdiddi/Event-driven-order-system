package com.rohithdiddi.orderservice.service;

import com.rohithdiddi.orderservice.kafka.OrderEventPublisher;
import com.rohithdiddi.orderservice.model.CreateOrderRequest;
import com.rohithdiddi.orderservice.model.Order;
import com.rohithdiddi.orderservice.model.OrderCreatedEvent;
import com.rohithdiddi.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository, OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        Order order = new Order(
                request.productSku(),
                request.quantity(),
                request.totalPrice(),
                request.customerId()
        );
        Order saved = orderRepository.save(order);

        // Publish event so inventory-service can reserve stock asynchronously.
        eventPublisher.publish(OrderCreatedEvent.from(saved));

        return saved;
    }

    public Order getOrder(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + id));
    }
}
