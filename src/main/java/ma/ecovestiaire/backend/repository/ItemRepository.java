package ma.ecovestiaire.backend.repository;

import ma.ecovestiaire.backend.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}