package ma.ecovestiaire.backend.service.impl;

import ma.ecovestiaire.backend.dto.ItemRequest;
import ma.ecovestiaire.backend.dto.ItemResponse;
import ma.ecovestiaire.backend.entity.Category;
import ma.ecovestiaire.backend.entity.Item;
import ma.ecovestiaire.backend.entity.User;
import ma.ecovestiaire.backend.enums.ItemStatus;
import ma.ecovestiaire.backend.repository.CategoryRepository;
import ma.ecovestiaire.backend.repository.ItemRepository;
import ma.ecovestiaire.backend.repository.UserRepository;
import ma.ecovestiaire.backend.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public ItemServiceImpl(ItemRepository itemRepository,
                           CategoryRepository categoryRepository,
                           UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Utilisateur introuvable"
                ));
    }

    private Category getCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Catégorie introuvable"
                ));
    }

    private Item getItemOrThrow(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Article introuvable"
                ));
    }

    private ItemResponse toItemResponse(Item item) {
        ItemResponse dto = new ItemResponse();
        dto.setId(item.getId());
        dto.setTitle(item.getTitle());
        dto.setDescription(item.getDescription());
        dto.setPrice(item.getPrice());
        dto.setSize(item.getSize());
        dto.setConditionLabel(item.getConditionLabel());
        // si ItemResponse.status est un String
        dto.setStatus(item.getStatus().name());
        dto.setPhotos(item.getPhotos());
        dto.setCategoryId(item.getCategory().getId());
        dto.setCategoryName(item.getCategory().getName());
        dto.setSellerId(item.getSeller().getId());
        dto.setSellerFirstName(item.getSeller().getFirstName());
        dto.setSellerLastName(item.getSeller().getLastName());
        return dto;
    }

    @Override
    public ItemResponse createItem(ItemRequest request, List<String> photoPaths) {
        User seller = getCurrentUser();
        Category category = getCategoryOrThrow(request.getCategoryId());

        Item item = Item.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .size(request.getSize())
                .conditionLabel(request.getConditionLabel())
                .status(ItemStatus.AVAILABLE)
                .photos(photoPaths)
                .category(category)
                .seller(seller)
                .build();

        Item saved = itemRepository.save(item);
        return toItemResponse(saved);
    }

    @Override
    public ItemResponse updateItem(Long id, ItemRequest request, List<String> photoPaths) {
        Item item = getItemOrThrow(id);

        Category category = getCategoryOrThrow(request.getCategoryId());

        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setSize(request.getSize());
        item.setConditionLabel(request.getConditionLabel());
        item.setCategory(category);
        if (photoPaths != null && !photoPaths.isEmpty()) {
            item.setPhotos(photoPaths);
        }

        Item saved = itemRepository.save(item);
        return toItemResponse(saved);
    }

    @Override
    public void deleteItem(Long id) {
        Item item = getItemOrThrow(id);
        itemRepository.delete(item);
    }

    @Override
    public ItemResponse getItemById(Long id) {
        Item item = getItemOrThrow(id);
        return toItemResponse(item);
    }

    @Override
    public List<ItemResponse> searchItems(
            Long categoryId,
            String size,
            String conditionLabel,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String text,
            boolean includeSold,
            int page,
            int sizePage
    ) {
        Pageable pageable = PageRequest.of(page, sizePage);

        Page<Item> itemsPage = itemRepository.searchItems(
                categoryId,
                size,
                conditionLabel,
                minPrice,
                maxPrice,
                text,
                includeSold,
                pageable
        );

        return itemsPage.stream()
                .map(this::toItemResponse)
                .toList();
    }
}