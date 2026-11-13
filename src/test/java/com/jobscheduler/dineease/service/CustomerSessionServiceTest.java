package com.jobscheduler.dineease.service;

import com.jobscheduler.dineease.entity.CustomerSession;
import com.jobscheduler.dineease.repository.CustomerSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerSessionServiceTest {

    private CustomerSessionRepository repository;
    private CustomerSessionService service;

    @BeforeEach
    void setUp() {
        repository = mock(CustomerSessionRepository.class);
        service = new CustomerSessionService(repository);
    }

    @Test
    void createSessionStoresHashAndReturnsOpaqueToken() {
        when(repository.save(any(CustomerSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String token = service.createSession("9876543210");

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals(43, token.length());

        ArgumentCaptor<CustomerSession> captor =
                ArgumentCaptor.forClass(CustomerSession.class);

        verify(repository).save(captor.capture());

        CustomerSession saved = captor.getValue();

        assertEquals("9876543210", saved.getPhone());
        assertNotNull(saved.getTokenHash());
        assertEquals(64, saved.getTokenHash().length());
        assertNotEquals(token, saved.getTokenHash());
        assertTrue(saved.getExpiresAt().isAfter(
                OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(29)));
        assertTrue(saved.getExpiresAt().isBefore(
                OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(31)));
    }

    @Test
    void getVerifiedPhoneReturnsNullForNullOrBlankToken() {
        assertNull(service.getVerifiedPhone(null));
        assertNull(service.getVerifiedPhone("  "));

        verifyNoInteractions(repository);
    }

    @Test
    void getVerifiedPhoneReturnsNullWhenTokenDoesNotExist() {
        when(repository.findByTokenHash(any(String.class)))
                .thenReturn(Optional.empty());

        assertNull(service.getVerifiedPhone("unknown-token"));

        verify(repository).findByTokenHash(any(String.class));
        verify(repository, never()).delete(any(CustomerSession.class));
    }

    @Test
    void getVerifiedPhoneReturnsPhoneForUnexpiredSession() {
        CustomerSession session = mock(CustomerSession.class);

        when(session.getPhone()).thenReturn("9876543210");
        when(session.getExpiresAt()).thenReturn(
                OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(10));
        when(repository.findByTokenHash(any(String.class)))
                .thenReturn(Optional.of(session));

        assertEquals(
                "9876543210",
                service.getVerifiedPhone("valid-session-token"));

        verify(repository, never()).delete(any(CustomerSession.class));
    }

    @Test
    void getVerifiedPhoneDeletesExpiredSession() {
        CustomerSession session = mock(CustomerSession.class);

        when(session.getExpiresAt()).thenReturn(
                OffsetDateTime.now(ZoneOffset.UTC).minusSeconds(1));
        when(repository.findByTokenHash(any(String.class)))
                .thenReturn(Optional.of(session));

        assertNull(service.getVerifiedPhone("expired-session-token"));

        verify(repository).delete(session);
    }

    @Test
    void invalidateSessionDeletesMatchingSession() {
        CustomerSession session = mock(CustomerSession.class);

        when(repository.findByTokenHash(any(String.class)))
                .thenReturn(Optional.of(session));

        service.invalidateSession("session-token");

        verify(repository).delete(session);
    }

    @Test
    void invalidateSessionIgnoresNullAndBlankTokens() {
        service.invalidateSession(null);
        service.invalidateSession("");
        service.invalidateSession("  ");

        verifyNoInteractions(repository);
    }
}