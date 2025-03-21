package org.storkforge.beatsbust.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.storkforge.beatsbust.entity.Album;
import org.storkforge.beatsbust.entity.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    Optional<Album> findBySpotifyAlbumId(String spotifyAlbumId);

    List<Album> findByTitleContainingOrArtistContaining(String title, String artist);

    List<Album> findByGenresContaining(Genre genre);

    @Query(value = "SELECT a FROM Album a ORDER BY a.popularity DESC LIMIT :limit")
    List<Album> findByOrderByPopularityDesc(@Param("limit") int limit);
}
