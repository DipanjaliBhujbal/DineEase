package com.jobscheduler.dineease.controller;

import com.jobscheduler.dineease.dto.UpdateOrderStatusRequest;
import com.jobscheduler.dineease.entity.AppUser;
import com.jobscheduler.dineease.entity.CustomerOrder;
import com.jobscheduler.dineease.entity.OrderStatus;
import com.jobscheduler.dineease.dto.AdminOrderResponse;
import com.jobscheduler.dineease.repository.AppUserRepository;
import com.jobscheduler.dineease.security.AuthenticatedAppUser;
import com.jobscheduler.dineease.service.OrderService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/partner/orders")
public class RestaurantPartnerOrderController {

    private final OrderService orderService;
    private final AppUserRepository appUserRepository;

    public RestaurantPartnerOrderController(
            OrderService orderService,
            AppUserRepository appUserRepository) {
        this.orderService = orderService;
        this.appUserRepository = appUserRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<AdminOrderResponse> getOrders(
            @AuthenticationPrincipal AuthenticatedAppUser principal) {

        Long restaurantId = getAssignedRestaurantId(principal);

        return orderService.getOrdersForRestaurant(restaurantId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PatchMapping("/{orderId}/status")
    @Transactional
    public AdminOrderResponse updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request,
            @AuthenticationPrincipal AuthenticatedAppUser principal) {

        Long restaurantId = getAssignedRestaurantId(principal);
        OrderStatus newStatus = request.status();

        if (newStatus == OrderStatus.OUT_FOR_DELIVERY
                || newStatus == OrderStatus.DELIVERED) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Restaurant partners cannot manage delivery statuses.");
        }

        CustomerOrder order =
                orderService.updatePartnerOrderStatus(
                        orderId, restaurantId, newStatus);

        return toResponse(order);
    }

    private Long getAssignedRestaurantId(
            AuthenticatedAppUser principal) {

        if (principal == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication required.");
        }

        AppUser user = appUserRepository.findById(
                        principal.getAppUserId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Partner account not found."));

        if (user.getRestaurant() == null
                || user.getRestaurant().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No restaurant is assigned to this partner.");
        }

        return user.getRestaurant().getId();
    }

    private AdminOrderResponse toResponse(CustomerOrder order) {
        List<AdminOrderResponse.OrderItemDetail> items =
                order.getItems().stream()
                        .map(item ->
                                new AdminOrderResponse.OrderItemDetail(
                                        item.getMenuItemId(),
                                        item.getMenuItemName(),
                                        item.getQuantity(),
                                        item.getUnitPrice(),
                                        item.getUnitPrice().multiply(
                                                java.math.BigDecimal.valueOf(
                                                        item.getQuantity()))
                                ))
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
}

