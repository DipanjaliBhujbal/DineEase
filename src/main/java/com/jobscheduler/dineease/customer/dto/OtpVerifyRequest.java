package com.jobscheduler.dineease.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OtpVerifyRequest(
        @NotBlank(message = "Phone number is required")
        @Pattern(
                regexp = "^[0-9]{10}$",
                message = "Enter a valid 10-digit Indian mobile number"
        )
        String phone,

        @NotBlank(message = "OTP is required")
        @Pattern(
                regexp = "^[0-9]{6}$",
                message = "OTP must contain exactly 6 digits"
        )
        String otp
) {
}