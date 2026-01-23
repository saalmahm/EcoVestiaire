package ma.ecovestiaire.backend.repository;

import ma.ecovestiaire.backend.entity.Order;
import ma.ecovestiaire.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByBuyer(User buyer);
    Page<Order> findByBuyer(User buyer, Pageable pageable);
    Page<Order> findByItem_Seller(User seller, Pageable pageable);
}