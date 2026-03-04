package ch.jug.feedback.config;

import ch.jug.feedback.model.AccessToken;
import ch.jug.feedback.repository.AccessTokenRepository;
import ch.jug.feedback.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final TokenService tokenService;
    private final AccessTokenRepository tokenRepository;

    public DataInitializer(TokenService tokenService, AccessTokenRepository tokenRepository) {
        this.tokenService = tokenService;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (tokenRepository.count() == 0) {
            AccessToken token = tokenService.createToken("Initial admin token");
            log.info("===========================================");
            log.info("Created initial access token: {}", token.getToken());
            log.info("Use this token to log in at /login");
            log.info("===========================================");
        }
    }
}
