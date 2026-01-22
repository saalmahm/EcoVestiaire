package ma.ecovestiaire.backend.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class CategoryResponse {

    private Long id;
    private String name;
    private String description;
    private String icon;
    private Instant createdAt;
    private Instant updatedAt;
}