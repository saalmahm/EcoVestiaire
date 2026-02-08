package ma.ecovestiaire.backend.repository;

import ma.ecovestiaire.backend.entity.Subscription;
import ma.ecovestiaire.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    boolean existsByFollowerAndFollowing(User follower, User following);

    Optional<Subscription> findByFollowerAndFollowing(User follower, User following);

    List<Subscription> findByFollowing(User following); // followers de ce user

    List<Subscription> findByFollower(User follower);   // users que ce user suit
}