package org.storkforge.beatsbust.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.storkforge.beatsbust.entity.FavouriteArtist;

import java.util.List;

public interface ArtistRepository extends JpaRepository<FavouriteArtist, Long> {
    List<FavouriteArtist> findByUserId(Long userId);
}
