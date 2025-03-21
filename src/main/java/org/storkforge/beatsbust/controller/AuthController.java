
package org.storkforge.beatsbust.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.storkforge.beatsbust.entity.User;
import org.storkforge.beatsbust.service.SpotifyAuthService;
import org.storkforge.beatsbust.service.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private SpotifyAuthService spotifyAuthService;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Register a new user
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> registerRequest) {
        try {
            User user = userService.registerUser(
                    registerRequest.get("username"),
                    registerRequest.get("email"),
                    registerRequest.get("password")
            );

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("userId", user.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Login with username and password
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.get("username"),
                            loginRequest.get("password")
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Here you would typically generate a JWT token

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            // response.put("token", jwtToken);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    /**
     * Initiate Spotify login
     */
    @GetMapping("/spotify/login")
    public RedirectView spotifyLogin() {
        String authUrl = spotifyAuthService.getAuthorizationUrl();
        return new RedirectView(authUrl);
    }

    /**
     * Handle Spotify callback
     */
    @GetMapping("/spotify/callback")
    public ResponseEntity<?> spotifyCallback(@RequestParam String code) {
        try {
            User user = spotifyAuthService.handleSpotifyCallback(code);

            // Here you would typically generate a JWT token

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Spotify login successful");
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            // response.put("token", jwtToken);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during Spotify authentication: " + e.getMessage());
        }
    }
}
