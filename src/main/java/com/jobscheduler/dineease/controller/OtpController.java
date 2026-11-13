package com.jobscheduler.dineease.customer.controller;

import com.jobscheduler.dineease.customer.dto.OtpRequest;
import com.jobscheduler.dineease.customer.dto.OtpVerifyRequest;
import com.jobscheduler.dineease.customer.service.OtpService;
import com.jobscheduler.dineease.service.CustomerSessionService;

import jakarta.validation.Valid;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/customer/auth")
@Profile("dev")
public class OtpController {

    private final OtpService otpService;
    private final CustomerSessionService customerSessionService;

    public OtpController(
            OtpService otpService,
            CustomerSessionService customerSessionService) {
        this.otpService = otpService;
        this.customerSessionService = customerSessionService;
    }

    @PostMapping("/request-otp")
    public ResponseEntity<String> requestOtp(
            @Valid @RequestBody OtpRequest request) {

        try {
            otpService.generateOtp(request.phone());
            return ResponseEntity.accepted()
                    .body("If the request is accepted, an OTP can be delivered through the configured development channel.");
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(429)
                    .body("Please wait before requesting another OTP.");
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(
            @Valid @RequestBody OtpVerifyRequest request) {

        boolean verified = otpService.verifyOtp(
                request.phone(), request.otp());

        if (!verified) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "OTP is invalid or expired."));
        }

        String token =
                customerSessionService.createSession(request.phone());

        return ResponseEntity.ok(
                Map.of(
                        "message", "Phone verified successfully.",
                        "token", token,
                        "expiresInSeconds", 1800
                )
        );
    }
}
