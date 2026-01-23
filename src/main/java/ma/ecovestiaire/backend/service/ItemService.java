package ma.ecovestiaire.backend.service;

import ma.ecovestiaire.backend.dto.ItemRequest;
import ma.ecovestiaire.backend.dto.ItemResponse;

import java.math.BigDecimal;
import java.util.List;

public interface ItemService {

    ItemResponse createItem(ItemRequest request, List<String> photoPaths);

    ItemResponse updateItem(Long id, ItemRequest request, List<String> photoPaths);

    void deleteItem(Long id);

    ItemResponse getItemById(Long id);

    List<ItemResponse> searchItems(
            Long categoryId,
            String size,
            String conditionLabel,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String text,
            boolean includeSold,
            int page,
            int sizePage
    );
}