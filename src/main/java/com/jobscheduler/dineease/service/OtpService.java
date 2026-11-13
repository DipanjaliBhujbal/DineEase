package com.jobscheduler.dineease.customer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Profile("dev")
public class OtpService {

    private static final int MAX_RESENDS = 3;
    private static final long RESEND_COOLDOWN_SECONDS = 60;
    private static final long RATE_LIMIT_WINDOW_SECONDS = 3600;

    private final SecureRandom secureRandom = new SecureRandom();
    private final ConcurrentMap<String, OtpChallenge> challenges =
            new ConcurrentHashMap<>();
    private final ConcurrentMap<String, RateLimitState> rateLimits =
            new ConcurrentHashMap<>();

    private final int expiryMinutes;
    private final int maxAttempts;
    private final DevelopmentOtpDeliveryService developmentOtpDeliveryService;
    private final Clock clock;

    @Autowired
    public OtpService(
            @Value("${dineease.otp.expiry-minutes:5}") int expiryMinutes,
            @Value("${dineease.otp.max-attempts:5}") int maxAttempts,
            DevelopmentOtpDeliveryService developmentOtpDeliveryService) {
        this(expiryMinutes, maxAttempts, developmentOtpDeliveryService, Clock.systemUTC());
    }

    OtpService(
            int expiryMinutes,
            int maxAttempts,
            DevelopmentOtpDeliveryService developmentOtpDeliveryService,
            Clock clock) {

        if (expiryMinutes < 1 || maxAttempts < 1) {
            throw new IllegalArgumentException(
                    "OTP expiry and attempt limit must be positive"
            );
        }

        this.expiryMinutes = expiryMinutes;
        this.maxAttempts = maxAttempts;
        this.developmentOtpDeliveryService = developmentOtpDeliveryService;
        this.clock = clock;
    }

    /**
     * Creates a development OTP.
     * Rate-limit history is separate from the OTP challenge.
     */
    public synchronized void generateOtp(String phone) {
        Instant now = clock.instant();
        RateLimitState previousLimit = rateLimits.get(phone);

        if (previousLimit == null
                || !now.isBefore(previousLimit.windowStartedAt()
                .plusSeconds(RATE_LIMIT_WINDOW_SECONDS))) {
            previousLimit = new RateLimitState(now, 0, null);
        }

        if (previousLimit.lastIssuedAt() != null) {
            if (now.isBefore(previousLimit.lastIssuedAt()
                    .plusSeconds(RESEND_COOLDOWN_SECONDS))) {
                throw new IllegalStateException(
                        "Please wait before requesting another OTP"
                );
            }

            if (previousLimit.resendCount() >= MAX_RESENDS) {
                throw new IllegalStateException(
                        "OTP resend limit reached. Try again later"
                );
            }

            previousLimit = new RateLimitState(
                    previousLimit.windowStartedAt(),
                    previousLimit.resendCount() + 1,
                    now
            );
        } else {
            previousLimit = new RateLimitState(
                    previousLimit.windowStartedAt(), 0, now
            );
        }

        // Retain the rate-limit state even if the challenge is later removed.
        rateLimits.put(phone, previousLimit);

        String otp = String.format(
                "%06d", secureRandom.nextInt(1_000_000)
        );

        try {
            developmentOtpDeliveryService.deliver(phone, otp);
        } catch (RuntimeException exception) {
            challenges.remove(phone);
            throw exception;
        }

        challenges.put(phone, new OtpChallenge(
                otp,
                now.plusSeconds(expiryMinutes * 60L),
                0
        ));
    }

    public synchronized boolean verifyOtp(String phone, String otp) {
        OtpChallenge challenge = challenges.get(phone);

        if (challenge == null) {
            return false;
        }

        Instant now = clock.instant();

        if (!now.isBefore(challenge.expiresAt())
                || challenge.attempts() >= maxAttempts) {
            challenges.remove(phone);
            return false;
        }

        int attempts = challenge.attempts() + 1;

        if (!challenge.otp().equals(otp)) {
            challenges.put(phone, challenge.withAttempts(attempts));
            return false;
        }

        challenges.remove(phone);
        return true;
    }

    private record RateLimitState(
            Instant windowStartedAt,
            int resendCount,
            Instant lastIssuedAt) {
    }

    private record OtpChallenge(
            String otp,
            Instant expiresAt,
            int attempts) {

        private OtpChallenge withAttempts(int newAttempts) {
            return new OtpChallenge(otp, expiresAt, newAttempts);
        }
    }
}