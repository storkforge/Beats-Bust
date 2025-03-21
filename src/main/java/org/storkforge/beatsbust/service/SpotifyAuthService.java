
package org.storkforge.beatsbust.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.storkforge.beatsbust.entity.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class SpotifyAuthService {

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    @Value("${spotify.redirect.uri}")
    private String redirectUri;

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Generate the Spotify authorization URL
     */
    public String getAuthorizationUrl() {
        return "https://accounts.spotify.com/authorize" +
                "?client_id=" + clientId +
                "&response_type=code" +
                "&redirect_uri=" + redirectUri +
                "&scope=user-read-private user-read-email user-top-read user-library-read";
    }

    /**
     * Exchange authorization code for access token
     */
    public Map<String, String> getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://accounts.spotify.com/api/token",
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            Map<String, String> tokenInfo = new HashMap<>();
            tokenInfo.put("access_token", root.path("access_token").asText());
            tokenInfo.put("refresh_token", root.path("refresh_token").asText());
            tokenInfo.put("expires_in", root.path("expires_in").asText());

            return tokenInfo;
        } catch (IOException e) {
            throw new RuntimeException("Error parsing Spotify token response", e);
        }
    }

    /**
     * Get user profile information from Spotify
     */
    public Map<String, String> getUserProfile(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>("", headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.spotify.com/v1/me",
                HttpMethod.GET,
                entity,
                String.class
        );

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("id", root.path("id").asText());
            userInfo.put("email", root.path("email").asText());
            userInfo.put("display_name", root.path("display_name").asText());

            return userInfo;
        } catch (IOException e) {
            throw new RuntimeException("Error parsing Spotify user profile", e);
        }
    }

    /**
     * Handle the complete Spotify login flow
     */
    public User handleSpotifyCallback(String code) {
        // Exchange code for token
        Map<String, String> tokenInfo = getAccessToken(code);
        String accessToken = tokenInfo.get("access_token");

        // Get user profile from Spotify
        Map<String, String> userProfile = getUserProfile(accessToken);

        // Create or update user in our system
        return userService.createOrUpdateUserFromSpotify(
                userProfile.get("id"),
                userProfile.get("email"),
                userProfile.get("display_name"),
                accessToken
        );
    }
}
