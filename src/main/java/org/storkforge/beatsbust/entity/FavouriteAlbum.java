
package org.storkforge.beatsbust.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "favorite_albums")
public class FavouriteAlbum {
    @EmbeddedId
    private FavouriteAlbumId id = new FavouriteAlbumId();

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("albumId")
    @JoinColumn(name = "album_id")
    private Album album;

    public FavouriteAlbum() {}

    public FavouriteAlbum(User user, Album album) {
        this.user = user;
        this.album = album;
        this.id = new FavouriteAlbumId(user.getId(), album.getId());
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
    }
    public Album getAlbum() {
        return album;
    }
    public void setAlbum(Album album) {
        this.album = album;
    }
}
