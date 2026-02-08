package ma.ecovestiaire.backend.service;

import ma.ecovestiaire.backend.dto.AdminUserResponse;
import ma.ecovestiaire.backend.dto.UpdateUserStatusRequest;
import org.springframework.data.domain.Page;

public interface AdminUserService {

    Page<AdminUserResponse> getUsers(String search, String status, int page, int size);

    void updateUserStatus(Long userId, UpdateUserStatusRequest request);

    void softDeleteUser(Long userId);
}