package com.example.Project.service.social;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.Project.dto.OAuthUserInfo;
import com.example.Project.helper.constants.SocialLoginType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GoogleOauth implements SocialOauth {

    @Value("${sns.google.url}")
    private String GOOGLE_SNS_BASE_URL;
    @Value("${sns.google.client.id}")
    private String GOOGLE_SNS_CLIENT_ID;
    @Value("${sns.google.callback.url}")
    private String GOOGLE_SNS_CALLBACK_URL;
    @Value("${sns.google.client.secret}")
    private String GOOGLE_SNS_CLIENT_SECRET;
    @Value("${sns.google.token.url}")
    private String GOOGLE_SNS_TOKEN_BASE_URL;
    @Value("${sns.google.userinfo.url}")
    private String GOOGLE_SNS_USERINFO_URL;

    @Override
    public String getOauthRedirectURL() {
        Map<String, Object> params = new HashMap<>();
        params.put("scope", "profile email");
        params.put("response_type", "code");
        params.put("client_id", GOOGLE_SNS_CLIENT_ID);
        params.put("redirect_uri", GOOGLE_SNS_CALLBACK_URL);

        String parameterString = params.entrySet().stream()
                .map(x -> x.getKey() + "=" + x.getValue())
                .collect(Collectors.joining("&"));

        return GOOGLE_SNS_BASE_URL + "?" + parameterString;
    }

    @Override
    public String requestAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", GOOGLE_SNS_CLIENT_ID);
        params.put("client_secret", GOOGLE_SNS_CLIENT_SECRET);
        params.put("redirect_uri", GOOGLE_SNS_CALLBACK_URL);
        params.put("grant_type", "authorization_code");

        System.out.println("Request Access Token - Code: " + code);
        System.out.println("Request Access Token - Client ID: " + GOOGLE_SNS_CLIENT_ID);
        System.out.println("Request Access Token - Redirect URI: " + GOOGLE_SNS_CALLBACK_URL);

        // Google은 application/x-www-form-urlencoded 형식을 요구함
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String parameterString = params.entrySet().stream()
                .map(x -> x.getKey() + "=" + x.getValue())
                .collect(Collectors.joining("&"));

        HttpEntity<String> entity = new HttpEntity<>(parameterString, headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                    GOOGLE_SNS_TOKEN_BASE_URL,
                    entity,
                    String.class
            );

            System.out.println("Token Response Status: " + responseEntity.getStatusCode());
            System.out.println("Token Response Body: " + responseEntity.getBody());

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
                String accessToken = jsonNode.get("access_token").asText();
                System.out.println("Access Token obtained: " + (accessToken != null ? "YES" : "NO"));
                return accessToken;
            }
        } catch (Exception e) {
            System.err.println("Error requesting access token: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                GOOGLE_SNS_USERINFO_URL,
                HttpMethod.GET,
                entity,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.getBody());

                System.out.println("Google User Info Response: " + response.getBody());

                // Google OAuth2 v2 API에서는 'id' 대신 'sub' 또는 'id'를 사용
                String providerId = jsonNode.has("sub")
                        ? jsonNode.get("sub").asText()
                        : (jsonNode.has("id") ? jsonNode.get("id").asText() : null);

                String email = jsonNode.has("email") ? jsonNode.get("email").asText() : null;
                String name = jsonNode.has("name") ? jsonNode.get("name").asText() : null;
                String profileImage = jsonNode.has("picture") ? jsonNode.get("picture").asText() : null;

                if (providerId == null) {
                    System.err.println("Provider ID not found in response");
                    return null;
                }

                return OAuthUserInfo.builder()
                        .provider("GOOGLE")
                        .providerId(providerId)
                        .email(email)
                        .name(name)
                        .profileImage(profileImage)
                        .build();
            } catch (Exception e) {
                System.err.println("Error parsing Google user info: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public SocialLoginType type() {
        return SocialLoginType.GOOGLE;
    }
}
