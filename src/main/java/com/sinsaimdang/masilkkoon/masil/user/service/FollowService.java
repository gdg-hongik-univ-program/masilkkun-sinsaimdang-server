package com.sinsaimdang.masilkkoon.masil.user.service;

import com.sinsaimdang.masilkkoon.masil.user.dto.FollowStatusDto;
import com.sinsaimdang.masilkkoon.masil.user.entity.Follow;
import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import com.sinsaimdang.masilkkoon.masil.user.repository.FollowRepository;
import com.sinsaimdang.masilkkoon.masil.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public Follow followUser(Long followerId, Long followingId) {
        log.info("팔로우 요청 - 팔로워 : {}, 팔로잉 : {}", followerId, followingId);

        if (followerId.equals(followingId)) {
            log.warn("스스로 팔로우 시도");
            throw new IllegalArgumentException("스스로를 팔로우 할 수 없습니다 ");
        }

        validateFollowRequest(followerId, followingId);

        User follower = userService.getUserEntity(followerId);
        User following = userService.getUserEntity(followingId);

        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            log.warn("중복 팔로우 시도 - 팔로워 :{}, 팔로잉 :{}", followerId, followingId);
            throw new IllegalArgumentException("이미 팔로우 하고 있는 사용자입니다.");
        }

        Follow follow = Follow.createFollowRelationship(follower, following);
        Follow savedFollow = followRepository.save(follow);

        follower.incrementFollowingCount();
        following.incrementFollowerCount();

        userRepository.save(follower);
        userRepository.save(following);

        log.info("팔로우 완료 FollowId :{}, 팔로워 :{} -> 팔로잉 : {}",savedFollow.getId(), follower.getNickname(), following.getNickname());

        return savedFollow;
    }

    @Transactional
    public void unfollowUser(Long followerId, Long followingId) {
        log.info("언팔로우 요청 - 팔로워: {}, 팔로잉: {}", followerId, followingId);

        int deletedCount = followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);

        if (deletedCount == 0) {
            log.warn("존재하지 않는 팔로우 관계 언팔로우 시도 - 팔로워: {}, 팔로잉: {}", followerId, followingId);
            throw new IllegalArgumentException("팔로우 관계가 존재하지 않습니다.");
        }

        User follower = userService.getUserEntity(followerId);
        User following = userService.getUserEntity(followingId);

        follower.decrementFollowingCount();
        following.decrementFollowerCount();

        log.info("언팔로우 완료 - 팔로워: {} -> 팔로잉: {}", follower.getNickname(), following.getNickname());
    }

    public boolean isFollowing(Long followerId, Long followingId) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    public boolean isMutualFollow(Long userId1, Long userId2) {
        return followRepository.existsMutualFollow(userId1, userId2);
    }

    private void validateFollowRequest(Long followerId, Long followingId) {
        if (followerId == null || followingId == null) {
            throw new IllegalArgumentException("사용자 ID는 null일 수 없습니다.");
        }
    }

    public FollowStatusDto getFollowStatus(Long userId) {
        User user = userService.getUserEntity(userId);

        return FollowStatusDto.from(user);
    }

    public Page<User> getFollowers(Long userId, Pageable pageable) {
        log.debug("팔로워 목록 조회 - 사용자 : {}, 페이지 : {}", userId, pageable.getPageNumber());

        if(!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 사용자 입니다 - Id : {}" + userId);
        }

        return followRepository.getFollowers(userId, pageable);
    }

    public Page<User> getFollowing(Long userId, Pageable pageable) {
        log.debug("팔로잉 목록 조회 - 사용자: {}, 페이지: {}", userId, pageable.getPageNumber());

        // 사용자 존재 여부 확인
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다: " + userId);
        }

        return followRepository.getFollowing(userId, pageable);
    }
}
