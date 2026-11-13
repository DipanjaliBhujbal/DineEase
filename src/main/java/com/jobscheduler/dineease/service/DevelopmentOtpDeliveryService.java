package com.jobscheduler.dineease.customer.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Service
@Profile("dev")
public class DevelopmentOtpDeliveryService {

    private final Path otpFile = Path.of("target", "dev-otp.txt");

    public synchronized void deliver(String phone, String otp) {
        try {
            Files.createDirectories(otpFile.getParent());

            String content = "Phone: " + phone + System.lineSeparator()
                    + "OTP: " + otp + System.lineSeparator();

            Files.writeString(
                    otpFile,
                    content,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Development OTP delivery failed", exception
            );
        }
    }
}
