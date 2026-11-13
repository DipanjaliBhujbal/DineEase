package com.jobscheduler.dineease.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CreateOrderRequest(
    @NotNull(message = "Restaurant ID is required")
      @Positive(message = "Restaurant ID must be positive") Long restaurantId,

        @NotBlank(message = "Customer name is required")
        @Size(max = 100)
        String customerName,

        @NotBlank(message = "Phone number is required")
        @Pattern(
                regexp = "^[6-9]\\d{9}$",
                message = "Enter a valid 10-digit Indian mobile number"
        )
        String phone,

        @NotBlank(message = "Delivery address is required")
        @Size(max = 500)
        String address,

        @NotBlank(message = "City is required")
        @Size(max = 100)
        String city,

        @NotBlank(message = "PIN code is required")
        @Pattern(
                regexp = "^\\d{6}$",
                message = "Enter a valid 6-digit PIN code"
        )
        String pincode,

        @NotEmpty(message = "An order must contain at least one item")
        @Valid
        List<OrderLineRequest> items

) {
    public record OrderLineRequest(

            @jakarta.validation.constraints.NotNull(
                    message = "Menu item ID is required"
            )
            @jakarta.validation.constraints.Positive(
                    message = "Menu item ID must be positive"
            )
            Long menuItemId,

            @jakarta.validation.constraints.NotNull(
                    message = "Quantity is required"
            )
            @jakarta.validation.constraints.Positive(
                    message = "Quantity must be at least 1"
            )
            @jakarta.validation.constraints.Max(
                    value = 50,
                    message = "Quantity cannot exceed 50"
            )
            Integer quantity

    ) {}
}
