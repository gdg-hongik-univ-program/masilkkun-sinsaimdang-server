package com.sinsaimdang.masilkkoon.masil.user.service;

import com.sinsaimdang.masilkkoon.masil.user.dto.UserDto;
import com.sinsaimdang.masilkkoon.masil.user.entity.Follow;
import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import com.sinsaimdang.masilkkoon.masil.user.repository.FollowRepository;
import com.sinsaimdang.masilkkoon.masil.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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

    /**
     * 다른 사용자를 팔로우합니다.
     *
     * @param followerId  팔로우를 요청한 사용자의 ID
     * @param followingId 팔로우할 대상 사용자의 ID
     * @return 생성된 Follow 엔티티
     * @throws IllegalArgumentException 스스로를 팔로우하거나 이미 팔로우한 경우
     */
    @Transactional
    public Follow followUser(Long followerId, Long followingId) {
        log.info("팔로우 요청 처리 시작 - 팔로워 : {} -> 팔로잉 : {}", followerId, followingId);

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

        log.info("팔로우 요청 처리 완료 FollowId :{}, 팔로워 :{} -> 팔로잉 : {}",savedFollow.getId(), followerId, followingId);
        return savedFollow;
    }

    /**
     * 다른 사용자를 언팔로우합니다.
     *
     * @param followerId  언팔로우를 요청한 사용자의 ID
     * @param followingId 언팔로우할 대상 사용자의 ID
     * @throws IllegalArgumentException 팔로우 관계가 존재하지 않는 경우
     */
    @Transactional
    public void unfollowUser(Long followerId, Long followingId) {
        log.info("언팔로우 요청 처리 시작 - 팔로워: {} -> 팔로잉: {}", followerId, followingId);

        int deletedCount = followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);

        if (deletedCount == 0) {
            log.warn("존재하지 않는 팔로우 관계 언팔로우 시도 - 팔로워: {} -> 팔로잉: {}", followerId, followingId);
            throw new IllegalArgumentException("팔로우 관계가 존재하지 않습니다.");
        }

        User follower = userService.getUserEntity(followerId);
        User following = userService.getUserEntity(followingId);

        follower.decrementFollowingCount();
        following.decrementFollowerCount();

        log.info("언팔로우 요청 처리 완료 - 팔로워: {} -> 팔로잉: {}", followerId, followingId);
    }

    /**
     * 특정 사용자의 팔로워 및 팔로잉 수를 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 사용자의 팔로우 정보 DTO
     */
    public UserDto getFollowStatus(Long userId) {
        log.info("팔로우 통계 요청 처리 시작 - 대상 {}", userId);

        User user = userService.getUserEntity(userId);

        log.info("팔로우 통계 요청 처리 완료 - 대상 {}", userId);
        return UserDto.from(user);
    }

    /**
     * 특정 사용자의 팔로워 목록을 조회합니다.
     *
     * @param userId   조회할 사용자의 ID
     * @param pageable 페이징 정보
     * @return 팔로워 목록
     */
    public Slice<User> getFollowers(Long userId, Pageable pageable) {
        log.debug("팔로워 목록 조회 요청 처리 시작 - 사용자 : {}, 페이지 : {}", userId, pageable.getPageNumber());

        if(!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 사용자 입니다 - Id : {}" + userId);
        }

        log.debug("팔로워 목록 조회 요청 처리 완료 - 사용자 : {}, 페이지 : {}", userId, pageable.getPageNumber());
        return followRepository.getFollowers(userId, pageable);
    }

    /**
     * 특정 사용자의 팔로잉 목록을 조회합니다.
     *
     * @param userId   조회할 사용자의 ID
     * @param pageable 페이징 정보
     * @return 팔로잉 목록
     */
    public Slice<User> getFollowing(Long userId, Pageable pageable) {
        log.debug("팔로잉 목록 조회 요청 처리 시작 - 사용자 : {}, 페이지 : {}", userId, pageable.getPageNumber());

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다: " + userId);
        }

        log.debug("팔로잉 목록 조회 요청 처리 완료 - 사용자 : {}, 페이지 : {}", userId, pageable.getPageNumber());
        return followRepository.getFollowing(userId, pageable);
    }

    /**
     * 특정 사용자가 다른 사용자를 팔로우하고 있는지 확인합니다.
     *
     * @param followerId  팔로워 ID
     * @param followingId 팔로잉 ID
     * @return 팔로우 여부
     */
    public boolean isFollowing(Long followerId, Long followingId) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    /**
     * 두 사용자가 맞팔로우 관계인지 확인합니다.
     *
     * @param userId1 사용자 ID 1
     * @param userId2 사용자 ID 2
     * @return 맞팔로우 여부
     */
    public boolean isMutualFollow(Long userId1, Long userId2) {
        return followRepository.existsMutualFollow(userId1, userId2);
    }

    /**
     * 팔로우 요청의 유효성을 검사합니다.
     *
     * @param followerId  팔로워 ID
     * @param followingId 팔로잉 ID
     * @throws IllegalArgumentException 사용자 ID가 null인 경우
     */
    private void validateFollowRequest(Long followerId, Long followingId) {
        if (followerId == null || followingId == null) {
            throw new IllegalArgumentException("사용자 ID는 null일 수 없습니다.");
        }
    }
}
