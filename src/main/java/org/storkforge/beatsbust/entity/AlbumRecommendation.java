
package org.storkforge.beatsbust.entity;

import jakarta.persistence.*;

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

    private String reason;

    // Constructors
    public AlbumRecommendation() {}

    public AlbumRecommendation(User user, Album album, String reason) {
        this.user = user;
        this.album = album;
        this.reason = reason;
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
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
}
