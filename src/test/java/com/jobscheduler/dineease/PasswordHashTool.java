package com.jobscheduler.dineease;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.nio.file.Files;
import java.nio.file.Path;

public class PasswordHashTool {
    public static void main(String[] args) throws Exception {
        String password = System.getenv("DINEEASE_TEST_PARTNER_PASSWORD");
        if (password == null || password.isBlank()) {
            throw new IllegalStateException(
                "Set DINEEASE_TEST_PARTNER_PASSWORD before running this utility."
            );
        }

        PasswordEncoder encoder =
            PasswordEncoderFactories.createDelegatingPasswordEncoder();

        Path output = Path.of("target", "partner-password-hash.txt");
        Files.createDirectories(output.getParent());
        Files.writeString(output, encoder.encode(password));
        System.out.println("Partner password hash written to " + output);
    }
}