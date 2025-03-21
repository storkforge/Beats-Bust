package org.storkforge.beatsbust.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.storkforge.beatsbust.entity.Album;
import org.storkforge.beatsbust.entity.Genre;
import org.storkforge.beatsbust.repository.AlbumRepository;
import org.storkforge.beatsbust.repository.GenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private GenreRepository genreRepository;

    /**
     * Find album by ID
     */
    public Optional<Album> findById(Long id) {
        return albumRepository.findById(id);
    }

    /**
     * Find album by Spotify ID
     */
    public Optional<Album> findBySpotifyId(String spotifyId) {
        return albumRepository.findBySpotifyAlbumId(spotifyId);
    }

    /**
     * Save a new album
     */
    public Album save(Album album) {
        return albumRepository.save(album);
    }

    /**
     * Find albums by genre
     */
    public List<Album> findByGenre(String genreName) {
        Optional<Genre> genre = genreRepository.findByName(genreName);
        if (genre.isPresent()) {
            return albumRepository.findByGenresContaining(genre.get());
        }
        return List.of();
    }

    /**
     * Search albums by title or artist
     */
    public List<Album> searchAlbums(String query) {
        return albumRepository.findByTitleContainingOrArtistContaining(query, query);
    }

    /**
     * Get popular albums
     */
    public List<Album> getPopularAlbums(int limit) {
        return albumRepository.findByOrderByPopularityDesc(limit);
    }

    /**
     * Add genres to an album
     */
    public Album addGenresToAlbum(Long albumId, Set<String> genreNames) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album not found"));

        for (String genreName : genreNames) {
            Genre genre = genreRepository.findByName(genreName)
                    .orElseGet(() -> {
                        Genre newGenre = new Genre();
                        newGenre.setName(genreName);
                        return genreRepository.save(newGenre);
                    });

            album.addGenre(genre);
        }

        return albumRepository.save(album);
    }
}
