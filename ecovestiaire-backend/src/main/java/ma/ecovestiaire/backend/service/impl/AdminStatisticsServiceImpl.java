package ma.ecovestiaire.backend.service.impl;

import ma.ecovestiaire.backend.dto.AdminStatisticsResponse;
import ma.ecovestiaire.backend.enums.UserStatus;
import ma.ecovestiaire.backend.repository.ItemRepository;
import ma.ecovestiaire.backend.repository.OrderRepository;
import ma.ecovestiaire.backend.repository.UserRepository;
import ma.ecovestiaire.backend.service.AdminStatisticsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminStatisticsServiceImpl implements AdminStatisticsService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

    public AdminStatisticsServiceImpl(UserRepository userRepository,
                                      ItemRepository itemRepository,
                                      OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public AdminStatisticsResponse getStatistics() {
        AdminStatisticsResponse dto = new AdminStatisticsResponse();

        long totalUsers = userRepository.countByDeletedFalse();
        long totalActiveUsers =
                userRepository.countByDeletedFalseAndStatus(UserStatus.ACTIVE);

        long totalItems = itemRepository.count();

        long totalOrders = orderRepository.count();
        BigDecimal totalRevenue = BigDecimal.ZERO;

        dto.setTotalUsers(totalUsers);
        dto.setTotalActiveUsers(totalActiveUsers);
        dto.setTotalItems(totalItems);
        dto.setTotalOrders(totalOrders);
        dto.setTotalRevenue(totalRevenue);

        // Articles par catégorie
        List<Object[]> rows = itemRepository.countItemsPerCategory();
        Map<String, Long> perCategory = new HashMap<>();
        for (Object[] row : rows) {
            String categoryName = (String) row[0];
            Long count = (Long) row[1];
            perCategory.put(categoryName, count);
        }
        dto.setItemsPerCategory(perCategory);

        return dto;
    }
}