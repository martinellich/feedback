package ch.jug.feedback.service;

import ch.jug.feedback.model.AccessToken;
import ch.jug.feedback.repository.AccessTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class TokenService {

    private final AccessTokenRepository tokenRepository;

    public TokenService(AccessTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public AccessToken createToken(String description) {
        AccessToken token = new AccessToken();
        token.setToken(UUID.randomUUID().toString());
        token.setDescription(description);
        return tokenRepository.save(token);
    }

    @Transactional(readOnly = true)
    public Optional<AccessToken> findByToken(String tokenValue) {
        return tokenRepository.findByToken(tokenValue);
    }

    public boolean validateAndConsumeToken(String tokenValue) {
        Optional<AccessToken> tokenOpt = tokenRepository.findByToken(tokenValue);
        if (tokenOpt.isEmpty()) {
            return false;
        }
        AccessToken token = tokenOpt.get();
        if (!token.isValid()) {
            return false;
        }
        token.setUsed(true);
        token.setUsedAt(LocalDateTime.now());
        tokenRepository.save(token);
        return true;
    }

    @Transactional(readOnly = true)
    public boolean isValidToken(String tokenValue) {
        return tokenRepository.findByToken(tokenValue)
                .map(AccessToken::isValid)
                .orElse(false);
    }
}
