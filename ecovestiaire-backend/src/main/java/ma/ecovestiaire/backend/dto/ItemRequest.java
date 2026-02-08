package ma.ecovestiaire.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemRequest {

    @NotBlank
    @Size(max = 150)
    private String title;

    @NotBlank
    @Size(max = 2000)
    private String description;

    @NotNull
    private BigDecimal price;

    @Size(max = 20)
    private String size;

    @Size(max = 50)
    private String conditionLabel;

    @NotNull
    private Long categoryId;
}