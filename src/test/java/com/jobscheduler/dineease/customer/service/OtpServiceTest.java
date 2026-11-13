package com.jobscheduler.dineease.customer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OtpServiceTest {

    private static final String PHONE = "9876543210";
    private static final Pattern SIX_DIGITS = Pattern.compile("^[0-9]{6}$");
    private static final Instant START = Instant.parse("2026-01-01T00:00:00Z");

    private DevelopmentOtpDeliveryService deliveryService;
    private MutableClock clock;
    private OtpService otpService;

    @BeforeEach
    void setUp() {
        deliveryService = mock(DevelopmentOtpDeliveryService.class);
        clock = new MutableClock(START);
        otpService = new OtpService(5, 3, deliveryService, clock);
    }

    @Test
    void generateOtpDeliversSixDigitCode() {
        otpService.generateOtp(PHONE);

        var captor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(deliveryService).deliver(eq(PHONE), captor.capture());

        assertTrue(SIX_DIGITS.matcher(captor.getValue()).matches());
    }

    @Test
    void correctOtpIsAcceptedOnlyOnce() {
        otpService.generateOtp(PHONE);

        var captor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(deliveryService).deliver(eq(PHONE), captor.capture());

        String otp = captor.getValue();

        assertTrue(otpService.verifyOtp(PHONE, otp));
        assertFalse(otpService.verifyOtp(PHONE, otp));
    }

    @Test
    void incorrectOtpIsRejectedAndCorrectOtpCanStillBeUsed() {
        otpService.generateOtp(PHONE);

        var captor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(deliveryService).deliver(eq(PHONE), captor.capture());

        String correctOtp = captor.getValue();
        String incorrectOtp = correctOtp.equals("000000") ? "000001" : "000000";

        assertFalse(otpService.verifyOtp(PHONE, incorrectOtp));
        assertTrue(otpService.verifyOtp(PHONE, correctOtp));
    }

    @Test
    void challengeIsRejectedAfterMaximumFailedAttempts() {
        otpService.generateOtp(PHONE);

        var captor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(deliveryService).deliver(eq(PHONE), captor.capture());

        String correctOtp = captor.getValue();
        String incorrectOtp = correctOtp.equals("000000") ? "000001" : "000000";

        for (int i = 0; i < 3; i++) {
            assertFalse(otpService.verifyOtp(PHONE, incorrectOtp));
        }

        assertFalse(otpService.verifyOtp(PHONE, correctOtp));
    }

    @Test
    void resendImmediatelyIsRejectedDuringCooldown() {
        otpService.generateOtp(PHONE);

        assertThrows(
                IllegalStateException.class,
                () -> otpService.generateOtp(PHONE)
        );

        verify(deliveryService, times(1)).deliver(eq(PHONE), anyString());
    }

    @Test
    void resendSucceedsAfterCooldown() {
        otpService.generateOtp(PHONE);
        clock.advanceSeconds(59);

        assertThrows(
                IllegalStateException.class,
                () -> otpService.generateOtp(PHONE)
        );

        clock.advanceSeconds(1);
        assertDoesNotThrow(() -> otpService.generateOtp(PHONE));

        verify(deliveryService, times(2)).deliver(eq(PHONE), anyString());
    }

    @Test
    void resendLimitPersistsAfterOtpChallengeIsConsumed() {
        otpService.generateOtp(PHONE);

        var captor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(deliveryService).deliver(eq(PHONE), captor.capture());
        assertTrue(otpService.verifyOtp(PHONE, captor.getValue()));

        for (int resend = 1; resend <= 3; resend++) {
            clock.advanceSeconds(60);
            otpService.generateOtp(PHONE);
        }

        clock.advanceSeconds(60);

        assertThrows(
                IllegalStateException.class,
                () -> otpService.generateOtp(PHONE)
        );

        verify(deliveryService, times(4)).deliver(eq(PHONE), anyString());
    }

    @Test
    void resendLimitResetsAfterOneHourWindow() {
        otpService.generateOtp(PHONE);

        for (int resend = 1; resend <= 3; resend++) {
            clock.advanceSeconds(60);
            otpService.generateOtp(PHONE);
        }

        clock.advance(Duration.ofHours(1));

        assertDoesNotThrow(() -> otpService.generateOtp(PHONE));

        verify(deliveryService, times(5)).deliver(eq(PHONE), anyString());
    }

    @Test
    void expiredOtpIsRejected() {
        otpService.generateOtp(PHONE);

        var captor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(deliveryService).deliver(eq(PHONE), captor.capture());

        clock.advance(Duration.ofMinutes(5));

        assertFalse(otpService.verifyOtp(PHONE, captor.getValue()));
    }

    @Test
    void failedDeliveryDoesNotLeaveUsableChallenge() {
        doThrow(new IllegalStateException("delivery failed"))
                .when(deliveryService).deliver(eq(PHONE), anyString());

        assertThrows(
                IllegalStateException.class,
                () -> otpService.generateOtp(PHONE)
        );

        reset(deliveryService);

        assertFalse(otpService.verifyOtp(PHONE, "123456"));
    }

    private static final class MutableClock extends Clock {

        private Instant currentInstant;

        private MutableClock(Instant initialInstant) {
            this.currentInstant = initialInstant;
        }

        @Override
        public ZoneId getZone() {
            return ZoneId.of("UTC");
        }

        @Override
        public Clock withZone(ZoneId zone) {
            if (!ZoneId.of("UTC").equals(zone)) {
                throw new UnsupportedOperationException(
                        "This test clock supports UTC only"
                );
            }
            return this;
        }

        @Override
        public Instant instant() {
            return currentInstant;
        }

        private void advanceSeconds(long seconds) {
            advance(Duration.ofSeconds(seconds));
        }

        private void advance(Duration duration) {
            currentInstant = currentInstant.plus(duration);
        }
    }
}