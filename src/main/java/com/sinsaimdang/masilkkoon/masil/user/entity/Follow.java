package com.sinsaimdang.masilkkoon.masil.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follwer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_follow_follower"))
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follwing_id", nullable = false, foreignKey = @ForeignKey(name = "fk_follow_follower"))
    private User following;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static Follow createFollowRelationship(User follower, User following) {
        return Follow.builder()
                .follower(follower)
                .following(following)
                .build();
    }

    public boolean isRelationshipBetween(Long followerId, Long followingId) {
        return this.follower.getId().equals(followerId) && this.following.getId().equals(followingId);
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
