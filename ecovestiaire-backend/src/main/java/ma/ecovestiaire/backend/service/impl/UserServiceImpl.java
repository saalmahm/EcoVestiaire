package ma.ecovestiaire.backend.service.impl;

import ma.ecovestiaire.backend.dto.UpdateUserProfileRequest;
import ma.ecovestiaire.backend.dto.UserProfileResponse;
import ma.ecovestiaire.backend.entity.User;
import ma.ecovestiaire.backend.repository.UserRepository;
import ma.ecovestiaire.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private User getCurrentUserEntity() {
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

    private UserProfileResponse toDto(User user, boolean includeEmail) {
        UserProfileResponse dto = new UserProfileResponse();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        if (includeEmail) {
            dto.setEmail(user.getEmail());
        }
        dto.setBio(user.getBio());
        dto.setLocation(user.getLocation());
        dto.setProfilePhotoUrl(user.getProfilePhotoUrl());
        dto.setRole(user.getRole().name());
        return dto;
    }

    @Override
    public UserProfileResponse getCurrentUserProfile() {
        User user = getCurrentUserEntity();
        return toDto(user, true); // profil courant → email inclus
    }

    @Override
    public UserProfileResponse updateCurrentUserProfile(UpdateUserProfileRequest request) {
        User user = getCurrentUserEntity();

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getLocation() != null) {
            user.setLocation(request.getLocation());
        }
        if (request.getProfilePhotoUrl() != null) {
            user.setProfilePhotoUrl(request.getProfilePhotoUrl());
        }

        User saved = userRepository.save(user);
        return toDto(saved, true);
    }

    @Override
    public UserProfileResponse getPublicUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Utilisateur introuvable"
                ));

        // profil public → pas d'email
        return toDto(user, false);
    }
}