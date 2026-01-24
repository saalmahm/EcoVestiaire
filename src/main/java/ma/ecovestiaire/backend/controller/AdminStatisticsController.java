package ma.ecovestiaire.backend.controller;

import ma.ecovestiaire.backend.dto.AdminStatisticsResponse;
import ma.ecovestiaire.backend.service.AdminStatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminStatisticsController {

    private final AdminStatisticsService adminStatisticsService;

    public AdminStatisticsController(AdminStatisticsService adminStatisticsService) {
        this.adminStatisticsService = adminStatisticsService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<AdminStatisticsResponse> getStatistics() {
        AdminStatisticsResponse stats = adminStatisticsService.getStatistics();
        return ResponseEntity.ok(stats);
    }
}