
package org.storkforge.beatsbust.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;

    // Spotify login integration fields
    private String spotifyId;
    private String spotifyAccessToken;

    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FavouriteAlbum> favouriteAlbums = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_genres",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> favoriteGenres = new HashSet<>();

    public User() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    public String getSpotifyAccessToken() {
        return spotifyAccessToken;
    }

    public void setSpotifyAccessToken(String spotifyAccessToken) {
        this.spotifyAccessToken = spotifyAccessToken;
    }

    public Set<FavouriteAlbum> getFavoriteAlbums() {
        return favouriteAlbums;
    }

    public void setFavoriteAlbums(Set<FavouriteAlbum> favoriteAlbums) {
        this.favouriteAlbums = favoriteAlbums;
    }

    public Set<Genre> getFavoriteGenres() {
        return favoriteGenres;
    }

    public void setFavoriteGenres(Set<Genre> favoriteGenres) {
        this.favoriteGenres = favoriteGenres;
    }

    // Helper methods
    public void addFavoriteAlbum(Album album) {
        FavouriteAlbum favoriteAlbum = new FavouriteAlbum(this, album);
        favouriteAlbums.add(favoriteAlbum);
    }

    public void removeFavoriteAlbum(Album album) {
        favouriteAlbums.removeIf(favoriteAlbum -> favoriteAlbum.getAlbum().equals(album));
    }

    public void addFavoriteGenre(Genre genre) {
        favoriteGenres.add(genre);
    }

    public void removeFavoriteGenre(Genre genre) {
        favoriteGenres.remove(genre);
    }
}
