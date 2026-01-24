package ma.ecovestiaire.backend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class AdminStatisticsResponse {

    private long totalUsers;
    private long totalActiveUsers;
    private long totalItems;
    private long totalOrders;
    private BigDecimal totalRevenue;

    // clé = nom catégorie, valeur = nb d’articles
    private Map<String, Long> itemsPerCategory;
}