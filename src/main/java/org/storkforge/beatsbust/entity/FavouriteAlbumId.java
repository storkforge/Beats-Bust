package org.storkforge.beatsbust.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FavouriteAlbumId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "album_id")
    private Long albumId;

    public FavouriteAlbumId() {
    }

    public FavouriteAlbumId(Long userId, Long albumId) {
        this.userId = userId;
        this.albumId = albumId;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    // equals and hashCode for composite key
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FavouriteAlbumId)) return false;
        FavouriteAlbumId that = (FavouriteAlbumId) o;
        return Objects.equals(getUserId(), that.getUserId()) &&
                Objects.equals(getAlbumId(), that.getAlbumId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getAlbumId());
    }
}
