package ma.ecovestiaire.backend.service;

import ma.ecovestiaire.backend.dto.FavoriteItemResponse;

import java.util.List;

public interface FavoriteService {

    void likeItem(Long itemId);

    void unlikeItem(Long itemId);

    List<FavoriteItemResponse> getMyFavoriteItems();
}