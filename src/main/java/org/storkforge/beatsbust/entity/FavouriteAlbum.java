
package org.storkforge.beatsbust.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "favorite_albums")
public class FavouriteAlbum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "album_id")
    private Album album;

    private LocalDateTime addedAt;

    // Optional: rating that user gave to this album (e.g., 1-5 stars)
    private Integer rating;

    public FavouriteAlbum() {
        this.addedAt = LocalDateTime.now();
    }

    public FavouriteAlbum(User user, Album album) {
        this.user = user;
        this.album = album;
        this.addedAt = LocalDateTime.now();
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

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FavouriteAlbum)) return false;
        FavouriteAlbum that = (FavouriteAlbum) o;
        return user != null && album != null &&
                user.equals(that.getUser()) &&
                album.equals(that.getAlbum());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
