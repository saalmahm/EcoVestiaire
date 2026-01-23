package ma.ecovestiaire.backend.service.impl;

import ma.ecovestiaire.backend.dto.FavoriteItemResponse;
import ma.ecovestiaire.backend.entity.Favorite;
import ma.ecovestiaire.backend.entity.Item;
import ma.ecovestiaire.backend.entity.User;
import ma.ecovestiaire.backend.repository.FavoriteRepository;
import ma.ecovestiaire.backend.repository.ItemRepository;
import ma.ecovestiaire.backend.repository.UserRepository;
import ma.ecovestiaire.backend.service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository,
                               ItemRepository itemRepository,
                               UserRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Utilisateur introuvable"
                ));
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Article introuvable"
                ));
    }

    private FavoriteItemResponse toFavoriteItemResponse(Item item) {
        FavoriteItemResponse dto = new FavoriteItemResponse();
        dto.setItemId(item.getId());
        dto.setTitle(item.getTitle());
        dto.setPrice(item.getPrice());
        String firstPhoto = (item.getPhotos() != null && !item.getPhotos().isEmpty())
                ? item.getPhotos().get(0)
                : null;
        dto.setImageUrl(firstPhoto);
        dto.setLikesCount(favoriteRepository.countByItem(item));
        return dto;
    }

    @Override
    public void likeItem(Long itemId) {
        User user = getCurrentUser();
        Item item = getItemOrThrow(itemId);

        if (favoriteRepository.existsByUserAndItem(user, item)) {
            return;
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .item(item)
                .build();

        favoriteRepository.save(favorite);
    }

    @Override
    public void unlikeItem(Long itemId) {
        User user = getCurrentUser();
        Item item = getItemOrThrow(itemId);

        favoriteRepository.findByUserAndItem(user, item)
                .ifPresent(favoriteRepository::delete);
    }

    @Override
    public List<FavoriteItemResponse> getMyFavoriteItems() {
        User user = getCurrentUser();
        return favoriteRepository.findByUser(user)
                .stream()
                .map(Favorite::getItem)
                .map(this::toFavoriteItemResponse)
                .toList();
    }
}