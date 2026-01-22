package ma.ecovestiaire.backend.service.impl;

import ma.ecovestiaire.backend.dto.ItemRequest;
import ma.ecovestiaire.backend.dto.ItemResponse;
import ma.ecovestiaire.backend.entity.Category;
import ma.ecovestiaire.backend.entity.Item;
import ma.ecovestiaire.backend.entity.User;
import ma.ecovestiaire.backend.enums.ItemStatus;
import ma.ecovestiaire.backend.enums.Role;
import ma.ecovestiaire.backend.repository.CategoryRepository;
import ma.ecovestiaire.backend.repository.ItemRepository;
import ma.ecovestiaire.backend.repository.UserRepository;
import ma.ecovestiaire.backend.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }

        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Utilisateur courant introuvable"
                ));
    }

    private ItemResponse toDto(Item item) {
        ItemResponse dto = new ItemResponse();
        dto.setId(item.getId());
        dto.setTitle(item.getTitle());
        dto.setDescription(item.getDescription());
        dto.setPrice(item.getPrice());
        dto.setSize(item.getSize());
        dto.setConditionLabel(item.getConditionLabel());
        dto.setStatus(item.getStatus().name());
        dto.setCategoryId(item.getCategory().getId());
        dto.setCategoryName(item.getCategory().getName());
        dto.setSellerId(item.getSeller().getId());
        dto.setSellerFirstName(item.getSeller().getFirstName());
        dto.setSellerLastName(item.getSeller().getLastName());
        dto.setPhotos(item.getPhotos());
        dto.setCreatedAt(item.getCreatedAt());
        dto.setUpdatedAt(item.getUpdatedAt());
        return dto;
    }

    @Override
    public ItemResponse createItem(ItemRequest request, List<String> photoPaths) {
        User seller = getCurrentUser();

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Catégorie introuvable"
                ));

        Item item = Item.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .size(request.getSize())
                .conditionLabel(request.getConditionLabel())
                .status(ItemStatus.AVAILABLE)
                .category(category)
                .seller(seller)
                .photos(photoPaths)
                .build();

        Item saved = itemRepository.save(item);
        return toDto(saved);
    }

    @Override
    public ItemResponse updateItem(Long id, ItemRequest request, List<String> photoPaths) {
        User current = getCurrentUser();

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Article introuvable"
                ));

        boolean isSeller = item.getSeller().getId().equals(current.getId());
        boolean isAdmin = current.getRole() == Role.ADMIN;
        if (!isSeller && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
        }

        if (request.getTitle() != null) {
            item.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            item.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            item.setPrice(request.getPrice());
        }
        if (request.getSize() != null) {
            item.setSize(request.getSize());
        }
        if (request.getConditionLabel() != null) {
            item.setConditionLabel(request.getConditionLabel());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Catégorie introuvable"
                    ));
            item.setCategory(category);
        }
        if (photoPaths != null) {
            item.setPhotos(photoPaths);
        }

        Item saved = itemRepository.save(item);
        return toDto(saved);
    }

    @Override
    public void deleteItem(Long id) {
        User current = getCurrentUser();

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Article introuvable"
                ));

        boolean isSeller = item.getSeller().getId().equals(current.getId());
        boolean isAdmin = current.getRole() == Role.ADMIN;
        if (!isSeller && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
        }

        itemRepository.delete(item);
    }
}