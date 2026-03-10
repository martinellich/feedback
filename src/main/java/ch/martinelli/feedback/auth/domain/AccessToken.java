package ch.martinelli.feedback.auth.domain;

import java.time.LocalDateTime;

public record AccessToken(Long id, String email, String token, boolean used, LocalDateTime createdAt,
                           LocalDateTime expiresAt) {

    public AccessToken withId(Long id) {
        return new AccessToken(id, email, token, used, createdAt, expiresAt);
    }

    public AccessToken withUsed(boolean used) {
        return new AccessToken(id, email, token, used, createdAt, expiresAt);
    }
}
