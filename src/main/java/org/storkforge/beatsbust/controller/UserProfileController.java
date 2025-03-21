
package org.storkforge.beatsbust.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.storkforge.beatsbust.entity.Album;
import org.storkforge.beatsbust.entity.User;
import org.storkforge.beatsbust.service.AlbumService;
import org.storkforge.beatsbust.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private AlbumService albumService;

    /**
     * Get current user profile
     */
    @GetMapping
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("username", user.getUsername());
        profile.put("email", user.getEmail());
        profile.put("spotifyConnected", user.getSpotifyId() != null);

        return ResponseEntity.ok(profile);
    }

    /**
     * Get user's favorite albums
     */
    @GetMapping("/favorites")
    public ResponseEntity<?> getFavoriteAlbums(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<Album> favoriteAlbums = user.getFavoriteAlbums().stream()
                .map(favoriteAlbum -> favoriteAlbum.getAlbum())
                .collect(Collectors.toSet());

        return ResponseEntity.ok(favoriteAlbums);
    }

    /**
     * Add an album to favorites
     */
    @PostMapping("/favorites/{albumId}")
    public ResponseEntity<?> addFavoriteAlbum(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long albumId) {

        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Album album = albumService.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album not found"));

        user.addFavoriteAlbum(album);
        userService.save(user);

        return ResponseEntity.ok().body(Map.of("message", "Album added to favorites"));
    }

    /**
     * Remove an album from favorites
     */
    @DeleteMapping("/favorites/{albumId}")
    public ResponseEntity<?> removeFavoriteAlbum(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long albumId) {

        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Album album = albumService.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album not found"));

        user.removeFavoriteAlbum(album);
        userService.save(user);

        return ResponseEntity.ok().body(Map.of("message", "Album removed from favorites"));
    }

    /**
     * Update user profile
     */
    @PutMapping
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> profileUpdate) {

        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (profileUpdate.containsKey("username")) {
            user.setUsername(profileUpdate.get("username"));
        }

        if (profileUpdate.containsKey("email")) {
            user.setEmail(profileUpdate.get("email"));
        }

        userService.save(user);

        return ResponseEntity.ok().body(Map.of("message", "Profile updated successfully"));
    }
}
