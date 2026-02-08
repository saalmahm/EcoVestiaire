// java
package ma.ecovestiaire.backend.service.impl;

import ma.ecovestiaire.backend.dto.AdminUserResponse;
import ma.ecovestiaire.backend.dto.UpdateUserStatusRequest;
import ma.ecovestiaire.backend.entity.User;
import ma.ecovestiaire.backend.enums.UserStatus;
import ma.ecovestiaire.backend.repository.UserRepository;
import ma.ecovestiaire.backend.service.AdminUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Set;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;

    public AdminUserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private AdminUserResponse toAdminUserResponse(User user) {
        AdminUserResponse dto = new AdminUserResponse();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setDeleted(user.isDeleted());
        dto.setStatus(user.getStatus());
        dto.setCreatedAt(user.getCreatedAt());

        if (user.getRole() != null) {
            dto.setRoles(Collections.singleton(user.getRole().name()));
        } else {
            dto.setRoles(Collections.emptySet());
        }

        return dto;
    }

    @Override
    public Page<AdminUserResponse> getUsers(String search, String status, int page, int size) {
        String searchTerm = (search == null) ? "" : search;
        PageRequest pageable = PageRequest.of(page, size);

        Page<User> usersPage;
        if (status != null && !status.isBlank()) {
            UserStatus userStatus;
            try {
                userStatus = UserStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Status invalide. Valeurs possibles : ACTIVE, SUSPENDED"
                );
            }
            usersPage = userRepository.findByDeletedFalseAndEmailContainingIgnoreCaseAndStatus(
                    searchTerm, userStatus, pageable
            );
        } else {
            usersPage = userRepository.findByDeletedFalseAndEmailContainingIgnoreCase(
                    searchTerm, pageable
            );
        }

        return usersPage.map(this::toAdminUserResponse);
    }

    @Override
    public void updateUserStatus(Long userId, UpdateUserStatusRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Utilisateur introuvable"
                ));

        user.setStatus(request.getStatus());
        userRepository.save(user);
    }

    @Override
    public void softDeleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Utilisateur introuvable"
                ));

        user.setDeleted(true);
        userRepository.save(user);
    }
}
