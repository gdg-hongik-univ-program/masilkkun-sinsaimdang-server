package com.sinsaimdang.masilkkoon.masil.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 사용자 간의 팔로우 관계를 나타내는 엔티티 클래스
 */
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_follow_relationship",
                        columnNames = {"follower_id", "following_id"}
                )
        },
        indexes = {
                @Index(name = "idx_follower_id", columnList = "follower_id"),
                @Index(name = "idx_following_id", columnList = "following_id"),
                @Index(name = "idx_created_at", columnList = "created_at")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Follow {

    /**
     * 팔로우 관계의 PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 팔로우를 하는 사용자 (팔로워)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false, foreignKey = @ForeignKey(name = "fk_follow_follower"))
    private User follower;

    /**
     * 팔로우를 받는 사용자 (팔로잉)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", nullable = false, foreignKey = @ForeignKey(name = "fk_follow_following"))
    private User following;

    /**
     * 팔로우 관계 생성 시간
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 팔로우 관계를 생성
     *
     * @param follower 팔로우를 하는 사용자
     * @param following 팔로우를 받는 사용자
     * @return 생성된 Follow 객체
     */
    public static Follow createFollowRelationship(User follower, User following) {
        return Follow.builder()
                .follower(follower)
                .following(following)
                .build();
    }

    @Override
    public String toString() {
        return "Follow{" +
                "id=" + id +
                ", followerId=" + (follower != null ? follower.getId() : null) +
                ", followingId=" + (following != null ? following.getId() : null) +
                ", createdAt=" + createdAt +
                '}';
    }
}
