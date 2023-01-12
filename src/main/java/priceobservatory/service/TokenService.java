package priceobservatory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import priceobservatory.exception.BadRequestException;
import priceobservatory.model.Token;
import priceobservatory.repository.TokenRepository;

import java.util.Optional;

@Service
public class TokenService {
    @Autowired
    private TokenRepository tokenRepository;

    TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public Optional<Token> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public Token save(Token token) {
        return tokenRepository.save(token);
    }

    public void deleteById(Integer id) {
        tokenRepository.deleteById(id);
    }

    public String determineRole(String token) {
        Optional<Token> t = tokenRepository.findByToken(token);
        if (t.isPresent()) {
            return t.get().getRole();
        } else {
            throw new BadRequestException("Invalid token");
        }
    }
}
