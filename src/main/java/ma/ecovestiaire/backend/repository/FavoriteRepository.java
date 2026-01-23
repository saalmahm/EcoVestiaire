package ma.ecovestiaire.backend.repository;

import ma.ecovestiaire.backend.entity.Favorite;
import ma.ecovestiaire.backend.entity.Item;
import ma.ecovestiaire.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserAndItem(User user, Item item);

    Optional<Favorite> findByUserAndItem(User user, Item item);

    List<Favorite> findByUser(User user);

    long countByItem(Item item);
}