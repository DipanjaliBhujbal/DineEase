package com.jobscheduler.dineease.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AdminOrderResponse(
        Long orderId,
        String customerName,
        String phone,
        String address,
        String city,
        String pincode,
        BigDecimal totalAmount,
        String status,
        LocalDateTime createdAt,
        List<OrderItemDetail> items
) {

    public record OrderItemDetail(
            Long menuItemId,
            String menuItemName,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal lineTotal
    ) {
    }
}
