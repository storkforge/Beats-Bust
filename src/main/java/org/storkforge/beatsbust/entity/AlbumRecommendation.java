
package org.storkforge.beatsbust.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "album_recommendations")
public class AlbumRecommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "album_id")
    private Album album;

    private Double confidenceScore;
    private String recommendationReason;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private RecommendationStatus status = RecommendationStatus.PENDING;

    public enum RecommendationStatus {
        PENDING,
        LIKED,
        DISLIKED,
        SKIPPED
    }

    public AlbumRecommendation() {
        this.createdAt = LocalDateTime.now();
    }

    public AlbumRecommendation(User user, Album album, Double confidenceScore, String recommendationReason) {
        this.user = user;
        this.album = album;
        this.confidenceScore = confidenceScore;
        this.recommendationReason = recommendationReason;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getRecommendationReason() {
        return recommendationReason;
    }

    public void setRecommendationReason(String recommendationReason) {
        this.recommendationReason = recommendationReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public RecommendationStatus getStatus() {
        return status;
    }

    public void setStatus(RecommendationStatus status) {
        this.status = status;
    }
}
