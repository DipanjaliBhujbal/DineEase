package com.jobscheduler.dineease.customer.controller;

import com.jobscheduler.dineease.dto.OrderResponse;
import com.jobscheduler.dineease.entity.CustomerOrder;
import com.jobscheduler.dineease.entity.OrderItem;
import com.jobscheduler.dineease.service.CustomerSessionService;
import com.jobscheduler.dineease.service.OrderService;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/orders")
@Profile("dev")
public class CustomerOrderTrackingController {

    private final OrderService orderService;
    private final CustomerSessionService customerSessionService;

    public CustomerOrderTrackingController(
            OrderService orderService,
            CustomerSessionService customerSessionService) {
        this.orderService = orderService;
        this.customerSessionService = customerSessionService;
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(
            @PathVariable Long orderId,
            @RequestHeader(value = "X-Customer-Session", required = false)
            String sessionToken) {

        String verifiedPhone =
                customerSessionService.getVerifiedPhone(sessionToken);

        if (verifiedPhone == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(java.util.Map.of(
                            "message", "Please verify your phone number first."));
        }

        if (orderId == null || orderId <= 0) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("message", "Invalid order ID."));
        }

        final CustomerOrder order;
        try {
            order = orderService.getOrderById(orderId);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(java.util.Map.of("message", "Order not found."));
        }

        if (order.getPhone() == null ||
                !order.getPhone().equals(verifiedPhone)) {
            // Do not disclose whether another customer's order exists.
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(java.util.Map.of("message", "Order not found."));
        }

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

    private OrderResponse.OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderResponse.OrderItemResponse(
                item.getMenuItemId(),
                item.getMenuItemName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getUnitPrice().multiply(
                        java.math.BigDecimal.valueOf(item.getQuantity()))
        );
    }
}