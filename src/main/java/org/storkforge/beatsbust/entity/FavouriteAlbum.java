package org.storkforge.beatsbust.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favourite_albums")
public class FavouriteAlbum {

    @EmbeddedId
    private FavouriteAlbumId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("albumId")
    @JoinColumn(name = "album_id")
    private Album album;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Default constructor
    public FavouriteAlbum() {
    }

    // Constructor with parameters
    public FavouriteAlbum(User user, Album album) {
        this.id = new FavouriteAlbumId(user.getId(), album.getId());
        this.user = user;
        this.album = album;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public FavouriteAlbumId getId() {
        return id;
    }

    public void setId(FavouriteAlbumId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (this.id == null) {
            this.id = new FavouriteAlbumId();
        }
        this.id.setUserId(user.getId());
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
        if (this.id == null) {
            this.id = new FavouriteAlbumId();
        }
        this.id.setAlbumId(album.getId());
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
