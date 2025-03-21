package org.storkforge.beatsbust.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.storkforge.beatsbust.entity.Album;
import org.storkforge.beatsbust.entity.Genre;
import org.storkforge.beatsbust.entity.User;
import org.storkforge.beatsbust.repository.AlbumRepository;
import org.storkforge.beatsbust.repository.GenreRepository;

import java.util.*;

@Service
public class SpotifyService {
    private static final Logger logger = LoggerFactory.getLogger(SpotifyService.class);

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private GenreRepository genreRepository;

    private static final String SPOTIFY_API_BASE_URL = "https://api.spotify.com/v1";
    private static final String TOKEN_URL = "https://accounts.spotify.com/api/token";

    /**
     * Search for albums on Spotify
     */
    public List<Map<String, Object>> searchAlbums(String query, int limit) {
        try {
            String accessToken = getAccessToken();
            HttpHeaders headers = createAuthHeaders(accessToken);

            String url = SPOTIFY_API_BASE_URL + "/search?q=" + query + "&type=album&limit=" + limit;
            logger.info("Searching Spotify for albums with query: {}", query);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

            Map<String, Object> responseBody = response.getBody();
            Map<String, Object> albums = (Map<String, Object>) responseBody.get("albums");
            List<Map<String, Object>> items = (List<Map<String, Object>>) albums.get("items");

            logger.info("Found {} albums on Spotify", items.size());
            return items;
        } catch (RestClientException e) {
            logger.error("Error searching Spotify: {}", e.getMessage(), e);
            return new ArrayList<>();
        } catch (Exception e) {
            logger.error("Unexpected error in searchAlbums: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Get album details from Spotify and save to database
     */
    public Album fetchAndSaveAlbumDetails(String spotifyAlbumId) {
        try {
            String accessToken = getAccessToken();
            HttpHeaders headers = createAuthHeaders(accessToken);

            String url = SPOTIFY_API_BASE_URL + "/albums/" + spotifyAlbumId;
            logger.info("Fetching album details from Spotify for ID: {}", spotifyAlbumId);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

            Map<String, Object> albumData = response.getBody();

            // Check if album already exists in our database
            Optional<Album> existingAlbum = albumRepository.findBySpotifyAlbumId(spotifyAlbumId);

            Album album;
            if (existingAlbum.isPresent()) {
                album = existingAlbum.get();
                logger.info("Album already exists in database, updating: {}", album.getTitle());
            } else {
                album = new Album();
                album.setTitle((String) albumData.get("name"));

                // Get artist name
                List<Map<String, Object>> artists = (List<Map<String, Object>>) albumData.get("artists");
                if (!artists.isEmpty()) {
                    album.setArtist((String) artists.get(0).get("name"));
                }

                // Get release year
                String releaseDate = (String) albumData.get("release_date");
                if (releaseDate != null && releaseDate.length() >= 4) {
                    album.setReleaseYear(Integer.parseInt(releaseDate.substring(0, 4)));
                }
                logger.info("Creating new album: {}", album.getTitle());
            }

            // Update Spotify-specific data
            List<Map<String, Object>> images = (List<Map<String, Object>>) albumData.get("images");
            String coverImageUrl = null;
            if (images != null && !images.isEmpty()) {
                coverImageUrl = (String) images.get(0).get("url");
            }

            Integer popularity = (Integer) albumData.get("popularity");
            String spotifyUri = (String) albumData.get("uri");

            // Get preview URL if available
            String previewUrl = null;
            Map<String, Object> tracksMap = (Map<String, Object>) albumData.get("tracks");
            if (tracksMap != null) {
                List<Map<String, Object>> tracks = (List<Map<String, Object>>) tracksMap.get("items");
                if (tracks != null && !tracks.isEmpty()) {
                    previewUrl = (String) tracks.get(0).get("preview_url");
                }
            }

            album.updateFromSpotifyData(spotifyAlbumId, spotifyUri, coverImageUrl, popularity, previewUrl);

            // Handle genres
            List<String> genreNames = (List<String>) albumData.get("genres");
            if (genreNames != null && !genreNames.isEmpty()) {
                for (String genreName : genreNames) {
                    Genre genre = genreRepository.findByNameIgnoreCase(genreName)
                            .orElseGet(() -> {
                                Genre newGenre = new Genre(genreName);
                                return genreRepository.save(newGenre);
                            });
                    album.addGenre(genre);
                }
            }

            Album savedAlbum = albumRepository.save(album);
            logger.info("Successfully saved album: {}", savedAlbum.getTitle());
            return savedAlbum;
        } catch (RestClientException e) {
            logger.error("Error fetching album from Spotify: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch album from Spotify", e);
        } catch (Exception e) {
            logger.error("Unexpected error in fetchAndSaveAlbumDetails: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process album data", e);
        }
    }

    /**
     * Get personalized recommendations for a user based on their Spotify data
     */
    public List<String> getRecommendationsForUser(User user, int limit) {
        try {
            if (user.getSpotifyAccessToken() == null) {
                logger.warn("User does not have Spotify access token");
                throw new IllegalStateException("User does not have Spotify access token");
            }

            HttpHeaders headers = createAuthHeaders(user.getSpotifyAccessToken());

            // First, get user's top tracks from Spotify
            String topTracksUrl = SPOTIFY_API_BASE_URL + "/me/top/tracks?limit=5";
            logger.info("Fetching top tracks for user: {}", user.getId());

            ResponseEntity<Map> topTracksResponse = restTemplate.exchange(
                    topTracksUrl, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

            List<Map<String, Object>> topTracks = (List<Map<String, Object>>)
                    topTracksResponse.getBody().get("items");

            // Extract track IDs for seed
            List<String> seedTrackIds = new ArrayList<>();
            for (Map<String, Object> track : topTracks) {
                if (seedTrackIds.size() < 5) { // Spotify allows max 5 seed tracks
                    seedTrackIds.add((String) track.get("id"));
                }
            }

            // Get recommendations based on seed tracks
            String seedTracksParam = String.join(",", seedTrackIds);
            String recommendationsUrl = SPOTIFY_API_BASE_URL +
                    "/recommendations?seed_tracks=" + seedTracksParam + "&limit=" + limit;

            logger.info("Fetching recommendations with seed tracks: {}", seedTracksParam);

            ResponseEntity<Map> recommendationsResponse = restTemplate.exchange(
                    recommendationsUrl, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

            List<Map<String, Object>> tracks = (List<Map<String, Object>>)
                    recommendationsResponse.getBody().get("tracks");

            // Extract album IDs from recommended tracks
            List<String> recommendedAlbumIds = new ArrayList<>();
            for (Map<String, Object> track : tracks) {
                Map<String, Object> album = (Map<String, Object>) track.get("album");
                String albumId = (String) album.get("id");
                if (!recommendedAlbumIds.contains(albumId)) {
                    recommendedAlbumIds.add(albumId);
                }
            }

            logger.info("Found {} recommended albums", recommendedAlbumIds.size());
            return recommendedAlbumIds;
        } catch (RestClientException e) {
            logger.error("Error getting recommendations from Spotify: {}", e.getMessage(), e);
            return new ArrayList<>();
        } catch (Exception e) {
            logger.error("Unexpected error in getRecommendationsForUser: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Get Spotify access token using client credentials flow
     */
    private String getAccessToken() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(clientId, clientSecret);
            headers.set("Content-Type", "application/x-www-form-urlencoded");

            String body = "grant_type=client_credentials";
            logger.debug("Requesting Spotify access token");

            ResponseEntity<Map> response = restTemplate.exchange(
                    TOKEN_URL, HttpMethod.POST, new HttpEntity<>(body, headers), Map.class);

            Map<String, Object> responseBody = response.getBody();
            logger.debug("Successfully obtained Spotify access token");
            return (String) responseBody.get("access_token");
        } catch (RestClientException e) {
            logger.error("Error getting Spotify access token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get Spotify access token", e);
        }
    }

    /**
     * Create HTTP headers with Bearer authentication
     */
    private HttpHeaders createAuthHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        return headers;
    }
}
