package ma.ecovestiaire.backend.controller;

import jakarta.validation.Valid;
import ma.ecovestiaire.backend.dto.UpdateUserProfileRequest;
import ma.ecovestiaire.backend.dto.UserProfileResponse;
import ma.ecovestiaire.backend.dto.UserSummaryResponse;
import ma.ecovestiaire.backend.service.SubscriptionService;
import ma.ecovestiaire.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final SubscriptionService subscriptionService;

    public UserController(UserService userService,
                          SubscriptionService subscriptionService) {
        this.userService = userService;
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateCurrentUserProfile(
            @Valid @RequestBody UpdateUserProfileRequest request) {
        return ResponseEntity.ok(userService.updateCurrentUserProfile(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getPublicUserProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getPublicUserProfile(id));
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<Void> followUser(@PathVariable("id") Long userId) {
        subscriptionService.followUser(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/follow")
    public ResponseEntity<Void> unfollowUser(@PathVariable("id") Long userId) {
        subscriptionService.unfollowUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<List<UserSummaryResponse>> getFollowers(@PathVariable("id") Long userId) {
        List<UserSummaryResponse> followers = subscriptionService.getFollowers(userId);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{id}/following")
    public ResponseEntity<List<UserSummaryResponse>> getFollowing(@PathVariable("id") Long userId) {
        List<UserSummaryResponse> following = subscriptionService.getFollowing(userId);
        return ResponseEntity.ok(following);
    }
}