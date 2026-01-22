package ma.ecovestiaire.backend.service;

import ma.ecovestiaire.backend.dto.ItemRequest;
import ma.ecovestiaire.backend.dto.ItemResponse;

import java.util.List;

public interface ItemService {

    ItemResponse createItem(ItemRequest request, List<String> photoPaths);

    ItemResponse updateItem(Long id, ItemRequest request, List<String> photoPaths);

    void deleteItem(Long id);
}