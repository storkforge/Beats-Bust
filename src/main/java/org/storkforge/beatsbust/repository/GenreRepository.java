
package org.storkforge.beatsbust.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.storkforge.beatsbust.entity.Genre;

import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    /**
     * Find a genre by its name, ignoring case sensitivity
     *
     * @param name the genre name to search for
     * @return an Optional containing the genre if found, or empty if not found
     */
    Optional<Genre> findByName(String name);

    Optional<Genre> findByNameIgnoreCase(String genreName);
}
