package ma.ecovestiaire.backend.controller;

import jakarta.validation.Valid;
import ma.ecovestiaire.backend.dto.AdminUserResponse;
import ma.ecovestiaire.backend.dto.UpdateUserStatusRequest;
import ma.ecovestiaire.backend.service.AdminUserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    // GET /admin/users?search=&status=&page=&size=
    @GetMapping
    public ResponseEntity<Page<AdminUserResponse>> getUsers(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        Page<AdminUserResponse> result =
                adminUserService.getUsers(search, status, page, size);
        return ResponseEntity.ok(result);
    }

    // PUT /admin/users/{id}/status
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable("id") Long userId,
            @Valid @RequestBody UpdateUserStatusRequest request
    ) {
        adminUserService.updateUserStatus(userId, request);
        return ResponseEntity.noContent().build();
    }

    // DELETE /admin/users/{id} (suppression logique)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long userId) {
        adminUserService.softDeleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}