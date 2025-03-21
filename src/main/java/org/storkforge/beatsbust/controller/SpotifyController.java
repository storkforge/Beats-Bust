package org.storkforge.beatsbust.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.storkforge.beatsbust.entity.Album;
import org.storkforge.beatsbust.entity.User;
import org.storkforge.beatsbust.service.SpotifyService;
import org.storkforge.beatsbust.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/spotify")
public class SpotifyController {

    @Autowired
    private SpotifyService spotifyService;

    @Autowired
    private UserService userService;

    /**
     * Search for albums on Spotify
     */
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchAlbums(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int limit) {

        List<Map<String, Object>> results = spotifyService.searchAlbums(query, limit);
        return ResponseEntity.ok(results);
    }

    /**
     * Import an album from Spotify to our database
     */
    @PostMapping("/albums/import")
    public ResponseEntity<Album> importAlbum(@RequestParam String spotifyAlbumId) {
        Album album = spotifyService.fetchAndSaveAlbumDetails(spotifyAlbumId);
        return ResponseEntity.ok(album);
    }

    /**
     * Get personalized recommendations from Spotify
     */
    @GetMapping("/recommendations")
    public ResponseEntity<List<Album>> getRecommendations(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "10") int limit) {

        // Get the full user entity with Spotify tokens
        User user = userService.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> recommendedAlbumIds = spotifyService.getRecommendationsForUser(user, limit);

        // Import all recommended albums
        List<Album> albums = recommendedAlbumIds.stream()
                .map(spotifyService::fetchAndSaveAlbumDetails)
                .collect(Collectors.toList());

        return ResponseEntity.ok(albums);
    }

    /**
     * Connect user account to Spotify
     */
    @PostMapping("/connect")
    public ResponseEntity<Void> connectSpotify(
            @AuthenticationPrincipal User currentUser,
            @RequestParam String spotifyId,
            @RequestParam String accessToken) {

        User user = userService.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setSpotifyId(spotifyId);
        user.setSpotifyAccessToken(accessToken);
        userService.save(user);

        return ResponseEntity.ok().build();
    }
}
