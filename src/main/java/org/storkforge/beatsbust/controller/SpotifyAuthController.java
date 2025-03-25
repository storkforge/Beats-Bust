package org.storkforge.beatsbust.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import org.storkforge.beatsbust.service.SpotifyAuthService;

@Controller
@RequestMapping("/api/spotify")
public class SpotifyAuthController {

    @Autowired
    private SpotifyAuthService spotifyAuthService;

    /**
     * Initiate Spotify authorization
     */
    @GetMapping("/authorize")
    public RedirectView authorizeSpotify() {
        String authUrl = spotifyAuthService.getAuthorizationUrl();
        return new RedirectView(authUrl);
    }

    /**
     * Handle Spotify callback
     */
    @GetMapping("/callback")
    public String spotifyCallback(@RequestParam String code) {
        try {
            spotifyAuthService.handleSpotifyCallback(code);
            return "redirect:/"; // Redirect to home page after successful authorization
        } catch (Exception e) {
            // Log the error
            e.printStackTrace();
            return "redirect:/?error=spotify_auth_failed";
        }
    }
}
