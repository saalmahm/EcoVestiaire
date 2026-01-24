package ma.ecovestiaire.backend.repository;

import ma.ecovestiaire.backend.entity.User;
import ma.ecovestiaire.backend.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<User> findByDeletedFalseAndEmailContainingIgnoreCaseAndStatus(
            String email, UserStatus status, Pageable pageable
    );

    Page<User> findByDeletedFalseAndEmailContainingIgnoreCase(
            String email, Pageable pageable
    );

    long countByDeletedFalse();

    long countByDeletedFalseAndStatus(UserStatus status);
}