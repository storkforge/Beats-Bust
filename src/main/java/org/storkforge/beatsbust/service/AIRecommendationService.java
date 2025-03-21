package org.storkforge.beatsbust.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AIRecommendationService {
    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AIRecommendationService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;

    }

    public List<String> getRecommendations(String userId, String context) {
        String apiURL = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        String prompt = generatePrompt(userId, context);

        Map<String, Object> requestBody = Map.of(
                "prompt", prompt,
                "max_tokens", 150,
                "temperature", 0.7
        );
        try {
            ResponseEntity<String> response = restTemplate.exchange(apiURL, HttpMethod.POST, new HttpEntity<>(requestBody, headers), String.class);
            return parseRecommendations(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    private List<String> parseRecommendations(String responseBody) {
        List<String> recommendations = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode choices = root.path("choices");
            if (choices.isArray()) {
                for (JsonNode choice : choices) {
                    String text = choice.path("message").path("content").asText();
                    String[] items = text.split(",");
                    for (String item : items) {
                        recommendations.add(item.trim());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recommendations;


    }

    private String generatePrompt(String userId, String context) {
        return "Given the user ID " + userId + " and the context " + context + ", generate a list of 3 recommendations for the user.";
    }


}
