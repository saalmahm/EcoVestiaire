package ma.ecovestiaire.backend.service;

import ma.ecovestiaire.backend.dto.UserSummaryResponse;

import java.util.List;

public interface SubscriptionService {

    void followUser(Long userIdToFollow);

    void unfollowUser(Long userIdToUnfollow);

    List<UserSummaryResponse> getFollowers(Long userId);

    List<UserSummaryResponse> getFollowing(Long userId);
}