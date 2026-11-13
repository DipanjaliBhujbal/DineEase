package com.jobscheduler.dineease.controller;

import com.jobscheduler.dineease.dto.AdminOrderResponse;
import com.jobscheduler.dineease.dto.UpdateOrderStatusRequest;
import com.jobscheduler.dineease.entity.CustomerOrder;
import com.jobscheduler.dineease.entity.OrderItem;
import com.jobscheduler.dineease.entity.OrderStatus;
import com.jobscheduler.dineease.service.OrderService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<AdminOrderResponse>> getAllOrders() {
        List<AdminOrderResponse> orders = orderService.getAllOrders()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<AdminOrderResponse> getOrderById(
            @PathVariable Long orderId) {

        CustomerOrder order = orderService.getAllOrders()
                .stream()
                .filter(existingOrder ->
                        existingOrder.getId().equals(orderId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found: " + orderId));

        return ResponseEntity.ok(toResponse(order));
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<AdminOrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        OrderStatus newStatus = request.status();

        CustomerOrder updatedOrder =
                orderService.updateOrderStatus(orderId, newStatus);

        return ResponseEntity.ok(toResponse(updatedOrder));
    }

    private AdminOrderResponse toResponse(CustomerOrder order) {
        List<AdminOrderResponse.OrderItemDetail> items =
                order.getItems()
                        .stream()
                        .map(this::toItemDetail)
                        .toList();

        return new AdminOrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getPhone(),
                order.getAddress(),
                order.getCity(),
                order.getPincode(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                items
        );
    }

    private AdminOrderResponse.OrderItemDetail toItemDetail(
            OrderItem item) {

        return new AdminOrderResponse.OrderItemDetail(
                item.getMenuItemId(),
                item.getMenuItemName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getUnitPrice()
                        .multiply(java.math.BigDecimal.valueOf(
                                item.getQuantity()))
        );
    }
}
