package ma.ecovestiaire.backend.repository;

import ma.ecovestiaire.backend.entity.Item;
import ma.ecovestiaire.backend.enums.ItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("""
           SELECT i FROM Item i
           WHERE i.status IN :statuses
             AND (:categoryId IS NULL OR i.category.id = :categoryId)
             AND (:size IS NULL OR i.size = :size)
             AND (:conditionLabel IS NULL OR i.conditionLabel = :conditionLabel)
             AND (:minPrice IS NULL OR i.price >= :minPrice)
             AND (:maxPrice IS NULL OR i.price <= :maxPrice)
             AND (:text IS NULL OR 1 = 1)
           """)
    Page<Item> searchItems(
            @Param("statuses") Iterable<ItemStatus> statuses,
            @Param("categoryId") Long categoryId,
            @Param("size") String size,
            @Param("conditionLabel") String conditionLabel,
            @Param("minPrice") java.math.BigDecimal minPrice,
            @Param("maxPrice") java.math.BigDecimal maxPrice,
            @Param("text") String text,
            Pageable pageable
    );
}