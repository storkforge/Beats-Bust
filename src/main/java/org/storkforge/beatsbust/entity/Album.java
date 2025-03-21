package org.storkforge.beatsbust.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "albums")
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String artist;
    private Integer releaseYear;
    private String coverImageUrl;


    private String spotifyAlbumId;
    private String spotifyUri;
    private Integer popularity;
    private String previewUrl;

    @ManyToMany
    @JoinTable(
            name = "album_genres",
            joinColumns = @JoinColumn(name = "album_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FavouriteAlbum> favoritedBy = new HashSet<>();

    public Album() {
    }

    public Album(String title, String artist, Integer releaseYear) {
        this.title = title;
        this.artist = artist;
        this.releaseYear = releaseYear;
    }

    //     Getters and Setters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getSpotifyAlbumId() {
        return spotifyAlbumId;
    }

    public void setSpotifyAlbumId(String spotifyAlbumId) {
        this.spotifyAlbumId = spotifyAlbumId;
    }

    public String getSpotifyUri() {
        return spotifyUri;
    }

    public void setSpotifyUri(String spotifyUri) {
        this.spotifyUri = spotifyUri;
    }

    public Integer getPopularity() {
        return popularity;
    }

    public void setPopularity(Integer popularity) {
        this.popularity = popularity;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    public Set<FavouriteAlbum> getFavoritedBy() {
        return favoritedBy;
    }

    public void setFavoritedBy(Set<FavouriteAlbum> favoritedBy) {
        this.favoritedBy = favoritedBy;
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
        genre.getAlbums().add(this);
    }

    public void removeGenre(Genre genre) {
        genres.remove(genre);
        genre.getAlbums().remove(this);
    }

    public void updateFromSpotifyData(String spotifyAlbumId, String spotifyUri,
                                      String coverImageUrl, Integer popularity,
                                      String previewUrl) {
        this.spotifyAlbumId = spotifyAlbumId;
        this.spotifyUri = spotifyUri;
        this.coverImageUrl = coverImageUrl;
        this.popularity = popularity;
        this.previewUrl = previewUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Album)) return false;
        Album album = (Album) o;
        return id != null && id.equals(album.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
