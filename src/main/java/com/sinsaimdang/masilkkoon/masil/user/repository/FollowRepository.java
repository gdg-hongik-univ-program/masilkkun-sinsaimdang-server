package com.sinsaimdang.masilkkoon.masil.user.repository;

import com.sinsaimdang.masilkkoon.masil.user.entity.Follow;
import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    /**
     * 특정 사용자가 다른 사용자를 팔로우하고 있는지 확인합니다.
     *
     * @param followerId  팔로워 ID
     * @param followingId 팔로잉 ID
     * @return 팔로우 여부
     */
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    /**
     * 특정 사용자의 팔로워 목록을 조회합니다.
     *
     * @param followingId 팔로잉 ID
     * @param pageable    페이징 정보
     * @return 팔로워의 User 객체 목록
     */
    @Query("SELECT f.follower FROM Follow f WHERE f.following.id = ?1")
    Slice<User> getFollowers(Long followingId, Pageable pageable);

    /**
     * 특정 사용자의 팔로잉 목록을 조회합니다.
     *
     * @param followerId 팔로워 ID
     * @param pageable   페이징 정보
     * @return 팔로잉의 User 객체 목록
     */
    @Query("SELECT f.following FROM Follow f WHERE f.follower.id = ?1")
    Slice<User> getFollowing(Long followerId, Pageable pageable);

    /**
     * 특정 팔로우 관계를 삭제합니다.
     *
     * @param followerId  팔로워 ID
     * @param followingId 팔로잉 ID
     * @return 삭제된 행의 수
     */
    @Modifying
    @Query("DELETE FROM Follow f WHERE f.follower.id = :followerId AND f.following.id = :followingId")
    int deleteByFollowerIdAndFollowingId(@Param("followerId") Long followerId,
                                         @Param("followingId") Long followingId);

    /**
     * 두 사용자 간의 맞팔로우 관계인지 확인합니다.
     *
     * @param userId1 사용자 ID 1
     * @param userId2 사용자 ID 2
     * @return 맞팔로우 여부
     */
    @Query("SELECT COUNT(f) = 2 FROM Follow f " +
            "WHERE (f.follower.id = :userId1 AND f.following.id = :userId2) " +
            "OR (f.follower.id = :userId2 AND f.following.id = :userId1)")
    boolean existsMutualFollow(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    /**
     * 특정 사용자와 관련된 모든 팔로우 관계를 삭제합니다.
     *
     * @param userId 사용자 ID
     */
    @Modifying
    @Query("DELETE FROM Follow f WHERE f.follower.id =:userId OR f.following.id =:userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
