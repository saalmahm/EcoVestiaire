package ma.ecovestiaire.backend.controller;

import jakarta.validation.Valid;
import ma.ecovestiaire.backend.dto.UpdateUserProfileRequest;
import ma.ecovestiaire.backend.dto.UserProfileResponse;
import ma.ecovestiaire.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET /api/users/me
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    // PUT /api/users/me
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateCurrentUserProfile(
            @Valid @RequestBody UpdateUserProfileRequest request) {
        return ResponseEntity.ok(userService.updateCurrentUserProfile(request));
    }

    // GET /api/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getPublicUserProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getPublicUserProfile(id));
    }
}