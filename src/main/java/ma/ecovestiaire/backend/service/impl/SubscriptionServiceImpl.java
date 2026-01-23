package ma.ecovestiaire.backend.service.impl;

import ma.ecovestiaire.backend.dto.UserSummaryResponse;
import ma.ecovestiaire.backend.entity.Subscription;
import ma.ecovestiaire.backend.entity.User;
import ma.ecovestiaire.backend.repository.SubscriptionRepository;
import ma.ecovestiaire.backend.repository.UserRepository;
import ma.ecovestiaire.backend.service.SubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository,
                                   UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Utilisateur introuvable"
                ));
    }

    private User getTargetUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Utilisateur à suivre introuvable"
                ));
    }

    private UserSummaryResponse toUserSummary(User user) {
        UserSummaryResponse dto = new UserSummaryResponse();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setProfilePhotoUrl(user.getProfilePhotoUrl());
        dto.setLocation(user.getLocation());
        return dto;
    }

    @Override
    public void followUser(Long userIdToFollow) {
        User current = getCurrentUser();
        User target = getTargetUser(userIdToFollow);

        if (current.getId().equals(target.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Vous ne pouvez pas vous suivre vous-même"
            );
        }

        if (subscriptionRepository.existsByFollowerAndFollowing(current, target)) {
            return;
        }

        Subscription subscription = Subscription.builder()
                .follower(current)
                .following(target)
                .build();

        subscriptionRepository.save(subscription);
    }

    @Override
    public void unfollowUser(Long userIdToUnfollow) {
        User current = getCurrentUser();
        User target = getTargetUser(userIdToUnfollow);

        subscriptionRepository.findByFollowerAndFollowing(current, target)
                .ifPresent(subscriptionRepository::delete);
    }

    @Override
    public List<UserSummaryResponse> getFollowers(Long userId) {
        User target = getTargetUser(userId);
        return subscriptionRepository.findByFollowing(target)
                .stream()
                .map(Subscription::getFollower)
                .map(this::toUserSummary)
                .toList();
    }

    @Override
    public List<UserSummaryResponse> getFollowing(Long userId) {
        User target = getTargetUser(userId);
        return subscriptionRepository.findByFollower(target)
                .stream()
                .map(Subscription::getFollowing)
                .map(this::toUserSummary)
                .toList();
    }
}