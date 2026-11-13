package com.jobscheduler.dineease.menu.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record MenuItemRequest(

        @NotBlank(message = "Menu item name is required")
        @Size(max = 150, message = "Name cannot exceed 150 characters")
        String name,

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.00", message = "Price cannot be negative")
        BigDecimal price,

        @Size(max = 500, message = "Image URL cannot exceed 500 characters")
        String imageUrl,

        Boolean available,

        @NotNull(message = "Category ID is required")
        Long categoryId
) {
}
