package com.jobscheduler.dineease;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEncoderTest {
    @Test
    void encodedPasswordCanBeVerified() {
        PasswordEncoder encoder =
            PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String password = "unit-test-only-password";
        String hash = encoder.encode(password);

        assertNotEquals(password, hash);
        assertTrue(encoder.matches(password, hash));
        assertFalse(encoder.matches("incorrect-password", hash));
    }
}