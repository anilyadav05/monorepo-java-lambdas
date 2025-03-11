package com.mycompany.lambdas.orderprocessor.service;

import com.mycompany.lambdas.orderprocessor.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    public Order processOrder(Order order) {
        logger.info("Processing order: {}", order.getId());

        // Validate the order
        validateOrder(order);

        // Calculate total if not already set
        if (order.getTotalAmount() == null || order.getTotalAmount().compareTo(BigDecimal.ZERO) == 0) {
            calculateTotal(order);
        }

        // Set initial status if not set
        if (order.getStatus() == null || order.getStatus().isEmpty()) {
            order.setStatus("RECEIVED");
        }

        // Set metadata
        order.getMetadata().setSource("order-processor");
        order.getMetadata().setEventType("ORDER_PROCESSED");

        logger.info("Order processed successfully: {}", order.getId());
        return order;
    }

    private void validateOrder(Order order) {
        if (order.getCustomerId() == null || order.getCustomerId().isEmpty()) {
            throw new IllegalArgumentException("Order must have a customer ID");
        }

        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }

        // Additional validation logic can be added here
    }

    private void calculateTotal(Order order) {
        BigDecimal total = order.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(total);
    }
}