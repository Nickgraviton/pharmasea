package priceobservatory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import priceobservatory.model.Token;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    Optional<Token> findByToken(String token);
}
