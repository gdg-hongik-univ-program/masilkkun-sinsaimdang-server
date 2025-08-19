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

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    @Query("SELECT f.follower FROM Follow f " +
            "WHERE f.following.id = :followingId " +
            "ORDER BY f.createdAt DESC")
    Slice<Follow> findFollowersByFollowingId(@Param("followingId") Long followingId, Pageable pageable);

    /**
     * 팔로워 User 목록 조회 (간단한 메서드명)
     */
    @Query("SELECT f.follower FROM Follow f WHERE f.following.id = ?1")
    Slice<User> getFollowers(Long followingId, Pageable pageable);

    /**
     * 팔로잉 User 목록 조회 (간단한 메서드명)
     */
    @Query("SELECT f.following FROM Follow f WHERE f.follower.id = ?1")
    Slice<User> getFollowing(Long followerId, Pageable pageable);

    @Query("SELECT f FROM Follow f " +
            "JOIN FETCH f.following " +
            "WHERE f.follower.id = :followerId " +
            "ORDER BY f.createdAt DESC")
    Slice<Follow> findFollowingsByFollowerId(@Param("followerId") Long followerId, Pageable pageable);

    @Query("SELECT f.following FROM Follow f " +
            "WHERE f.follower.id = :followerId " +
            "ORDER BY f.createdAt DESC")
    Slice<User> findFollowingUsersByFollowerId(@Param("followerId") Long followerId, Pageable pageable);

    long countByFollowingId(Long followingId);

    long countByFollowerId(Long followerId);

    @Modifying
    @Query("DELETE FROM Follow f WHERE f.follower.id = :followerId AND f.following.id = :followingId")
    int deleteByFollowerIdAndFollowingId(@Param("followerId") Long followerId,
                                         @Param("followingId") Long followingId);

    @Query("SELECT f FROM Follow f WHERE f.following.id IN :followingIds")
    List<Follow> findByFollowingIdIn(@Param("followingIds") List<Long> followingIds);

    @Query("SELECT COUNT(f) = 2 FROM Follow f " +
            "WHERE (f.follower.id = :userId1 AND f.following.id = :userId2) " +
            "OR (f.follower.id = :userId2 AND f.following.id = :userId1)")
    boolean existsMutualFollow(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}
