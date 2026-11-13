package com.jobscheduler.dineease.controller;

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
import java.util.Map;

@RestController
@RequestMapping("/api/customer")
@Profile("dev")
public class CustomerOrderHistoryController {

    private final CustomerSessionService customerSessionService;
    private final OrderService orderService;

    public CustomerOrderHistoryController(
            CustomerSessionService customerSessionService,
            OrderService orderService) {
        this.customerSessionService = customerSessionService;
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getOrderHistory(
            @RequestHeader(
                    value = "X-Customer-Session",
                    required = false
            ) String token) {

        String verifiedPhone =
                customerSessionService.getVerifiedPhone(token);

        if (verifiedPhone == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message",
                            "Please verify your phone number first."
                    ));
        }

        List<CustomerOrder> orders =
                orderService.getOrdersByPhone(verifiedPhone);

        List<OrderResponse> response = orders.stream()
                .map(order -> new OrderResponse(
                        order.getId(),
                        order.getCustomerName(),
                        order.getStatus(),
                        order.getTotalAmount(),
                        order.getCreatedAt(),
                        order.getItems().stream()
                                .map(this::toItemResponse)
                                .toList()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(
            @RequestHeader(
                    value = "X-Customer-Session",
                    required = false
            ) String token) {

        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Session token is required."));
        }

        customerSessionService.invalidateSession(token);

        return ResponseEntity.ok(
                Map.of("message", "Logged out successfully.")
        );
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
