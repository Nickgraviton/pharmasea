package priceobservatory.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import priceobservatory.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);
    void deleteByIdAndRoleNotLike(Integer id, String admin);
}
