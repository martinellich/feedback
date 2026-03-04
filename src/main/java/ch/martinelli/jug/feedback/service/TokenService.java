package ch.martinelli.jug.feedback.service;

import ch.martinelli.jug.feedback.entity.AccessToken;
import ch.martinelli.jug.feedback.repository.AccessTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    private final AccessTokenRepository tokenRepository;

    public TokenService(AccessTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    public String generateToken() {
        String tokenValue = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        AccessToken token = new AccessToken();
        token.setToken(tokenValue);
        token.setExpiresAt(LocalDateTime.now().plusDays(7));
        tokenRepository.save(token);
        return tokenValue;
    }

    @Transactional
    public boolean validateAndUseToken(String tokenValue) {
        Optional<AccessToken> optToken = tokenRepository.findByToken(tokenValue);
        if (optToken.isEmpty()) {
            return false;
        }
        AccessToken token = optToken.get();
        if (token.isUsed()) {
            return false;
        }
        if (token.getExpiresAt() != null && token.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }
        token.setUsed(true);
        tokenRepository.save(token);
        return true;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void generateInitialToken() {
        String token = generateToken();
        logger.info("=================================================");
        logger.info("Initial access token: {}", token);
        logger.info("Login at: http://localhost:8080/login");
        logger.info("=================================================");
    }
}
