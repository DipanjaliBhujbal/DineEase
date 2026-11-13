package com.jobscheduler.dineease.controller;

import com.jobscheduler.dineease.dto.CreateOrderRequest;
import com.jobscheduler.dineease.dto.OrderResponse;
import com.jobscheduler.dineease.entity.CustomerOrder;
import com.jobscheduler.dineease.entity.OrderItem;
import com.jobscheduler.dineease.service.OrderService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Create a new customer order.
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        CustomerOrder order = orderService.createOrder(request);

        List<OrderResponse.OrderItemResponse> responseItems =
                order.getItems().stream()
                        .map(this::toItemResponse)
                        .toList();

        OrderResponse response = new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                responseItems
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Get order details for customer tracking.
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long orderId) {

        if (orderId == null || orderId <= 0) {
            return ResponseEntity.badRequest().build();
        }

        CustomerOrder order = orderService.getOrderById(orderId);

        List<OrderResponse.OrderItemResponse> responseItems =
                order.getItems().stream()
                        .map(this::toItemResponse)
                        .toList();

        OrderResponse response = new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                responseItems
        );

        return ResponseEntity.ok(response);
    }

    private OrderResponse.OrderItemResponse toItemResponse(
            OrderItem item) {

        return new OrderResponse.OrderItemResponse(
                item.getMenuItemId(),
                item.getMenuItemName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getUnitPrice().multiply(
                        java.math.BigDecimal.valueOf(item.getQuantity())
                )
        );
    }
}
