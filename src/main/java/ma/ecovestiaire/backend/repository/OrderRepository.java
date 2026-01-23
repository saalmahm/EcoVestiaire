package ma.ecovestiaire.backend.repository;

import ma.ecovestiaire.backend.entity.Order;
import ma.ecovestiaire.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByBuyer(User buyer);
}