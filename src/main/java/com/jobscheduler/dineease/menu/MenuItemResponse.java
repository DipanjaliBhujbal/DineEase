package com.jobscheduler.dineease.menu.dto;

import java.math.BigDecimal;

public record MenuItemResponse(
        Long id,
        Long restaurantId,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        boolean available,
        Long categoryId,
        String categoryName
) {
}
