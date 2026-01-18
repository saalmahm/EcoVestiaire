package ma.ecovestiaire.backend.service;

import ma.ecovestiaire.backend.dto.UpdateUserProfileRequest;
import ma.ecovestiaire.backend.dto.UserProfileResponse;

public interface UserService {

    UserProfileResponse getCurrentUserProfile();

    UserProfileResponse updateCurrentUserProfile(UpdateUserProfileRequest request);

    UserProfileResponse getPublicUserProfile(Long userId);
}