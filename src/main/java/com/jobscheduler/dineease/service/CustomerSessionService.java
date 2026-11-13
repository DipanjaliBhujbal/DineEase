package com.jobscheduler.dineease.service;

import com.jobscheduler.dineease.entity.CustomerSession;
import com.jobscheduler.dineease.repository.CustomerSessionRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

@Service
@Profile("dev")
@Transactional
public class CustomerSessionService {

    private static final long SESSION_DURATION_SECONDS = 30 * 60;

    private final SecureRandom secureRandom = new SecureRandom();
    private final CustomerSessionRepository sessionRepository;

    public CustomerSessionService(
            CustomerSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public String createSession(String phone) {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        String tokenHash = hashToken(token);

        CustomerSession session = new CustomerSession(
                phone,
                tokenHash,
                OffsetDateTime.now(ZoneOffset.UTC)
                        .plusSeconds(SESSION_DURATION_SECONDS)
        );

        sessionRepository.save(session);

        // Return the raw token only to the caller.
        // Only its SHA-256 hash is stored in the database.
        return token;
    }

    public String getVerifiedPhone(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        String tokenHash = hashToken(token);
        Optional<CustomerSession> result =
                sessionRepository.findByTokenHash(tokenHash);

        if (result.isEmpty()) {
            return null;
        }

        CustomerSession session = result.get();

        if (!OffsetDateTime.now(ZoneOffset.UTC)
                .isBefore(session.getExpiresAt())) {
            sessionRepository.delete(session);
            return null;
        }

        return session.getPhone();
    }

    public void invalidateSession(String token) {
        if (token == null || token.isBlank()) {
            return;
        }

        sessionRepository.findByTokenHash(hashToken(token))
                .ifPresent(sessionRepository::delete);
    }

    private String hashToken(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 is not available.", exception);
        }
    }
}
