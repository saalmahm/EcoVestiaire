package ma.ecovestiaire.backend.repository;

import ma.ecovestiaire.backend.entity.Item;
import ma.ecovestiaire.backend.enums.ItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("""
            SELECT i FROM Item i
            WHERE (:categoryId IS NULL OR i.category.id = :categoryId)
              AND (:size IS NULL OR i.size = :size)
              AND (:conditionLabel IS NULL OR i.conditionLabel = :conditionLabel)
              AND (:minPrice IS NULL OR i.price >= :minPrice)
              AND (:maxPrice IS NULL OR i.price <= :maxPrice)
              AND (
                    :text IS NULL
                    OR LOWER(i.title) LIKE LOWER(CONCAT('%', :text, '%'))
                    OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%'))
                  )
              AND (:includeSold = TRUE OR i.status = 'AVAILABLE')
            """)
    Page<Item> searchItems(
            @Param("categoryId") Long categoryId,
            @Param("size") String size,
            @Param("conditionLabel") String conditionLabel,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("text") String text,
            @Param("includeSold") boolean includeSold,
            Pageable pageable
    );

    @Query("select i.category.name, count(i) from Item i group by i.category.name")
    List<Object[]> countItemsPerCategory();
}