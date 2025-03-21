
package org.storkforge.beatsbust.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.storkforge.beatsbust.entity.User;
import org.storkforge.beatsbust.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Find a user by their ID
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Find a user by their username
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Find a user by their email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Find a user by their Spotify ID
     */
    public Optional<User> findBySpotifyId(String spotifyId) {
        return userRepository.findBySpotifyId(spotifyId);
    }

    /**
     * Register a new user
     */
    public User registerUser(String username, String email, String password) {
        // Check if username or email already exists
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }

    /**
     * Connect a user account with Spotify
     */
    public User connectSpotifyAccount(Long userId, String spotifyId, String spotifyAccessToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setSpotifyId(spotifyId);
        user.setSpotifyAccessToken(spotifyAccessToken);

        return userRepository.save(user);
    }

    /**
     * Create or update a user from Spotify login
     */
    public User createOrUpdateUserFromSpotify(String spotifyId, String spotifyEmail,
                                              String spotifyUsername, String accessToken) {
        // Check if user with this Spotify ID already exists
        Optional<User> existingUser = userRepository.findBySpotifyId(spotifyId);

        if (existingUser.isPresent()) {
            // Update existing user
            User user = existingUser.get();
            user.setSpotifyAccessToken(accessToken);
            return userRepository.save(user);
        } else {
            // Create new user
            User newUser = new User();
            newUser.setSpotifyId(spotifyId);
            newUser.setSpotifyAccessToken(accessToken);

            // Use Spotify email and username if available
            if (spotifyEmail != null) {
                newUser.setEmail(spotifyEmail);
            }

            if (spotifyUsername != null) {
                newUser.setUsername(spotifyUsername);
            } else {
                // Generate a username based on Spotify ID if not available
                newUser.setUsername("spotify_user_" + spotifyId.substring(0, 8));
            }

            return userRepository.save(newUser);
        }
    }

    /**
     * Save user changes
     */
    public User save(User user) {
        return userRepository.save(user);
    }
}
