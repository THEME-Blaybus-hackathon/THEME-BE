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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.example.Project.dto.OAuthUserInfo;
import com.example.Project.helper.constants.SocialLoginType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoOauth implements SocialOauth {

    @Value("${sns.kakao.url}")
    private String KAKAO_SNS_BASE_URL;
    @Value("${sns.kakao.client.id}")
    private String KAKAO_SNS_CLIENT_ID;
    @Value("${sns.kakao.client.secret:#{null}}")
    private String KAKAO_SNS_CLIENT_SECRET;
    @Value("${sns.kakao.callback.url}")
    private String KAKAO_SNS_CALLBACK_URL;
    @Value("${sns.kakao.token.url}")
    private String KAKAO_SNS_TOKEN_BASE_URL;
    @Value("${sns.kakao.userinfo.url}")
    private String KAKAO_SNS_USERINFO_URL;

    @Override
    public String getOauthRedirectURL() {
        Map<String, Object> params = new HashMap<>();
        params.put("client_id", KAKAO_SNS_CLIENT_ID);
        params.put("redirect_uri", KAKAO_SNS_CALLBACK_URL);
        params.put("response_type", "code");
        // 카카오는 이메일 제공 안 함 - scope에서 account_email 제거
        params.put("scope", "profile_nickname,profile_image");

        String parameterString = params.entrySet().stream()
                .map(x -> x.getKey() + "=" + x.getValue())
                .collect(Collectors.joining("&"));

        return KAKAO_SNS_BASE_URL + "?" + parameterString;
    }

    @Override
    public String requestAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        System.out.println("=== Kakao Request Access Token ===");
        System.out.println("Code: " + code);
        System.out.println("Client ID: " + KAKAO_SNS_CLIENT_ID);
        System.out.println("Client Secret: " + (KAKAO_SNS_CLIENT_SECRET != null ? "EXISTS" : "NULL"));
        System.out.println("Redirect URI: " + KAKAO_SNS_CALLBACK_URL);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", KAKAO_SNS_CLIENT_ID);
        params.add("redirect_uri", KAKAO_SNS_CALLBACK_URL);
        params.add("code", code);

        // Client Secret이 설정되어 있으면 추가
        if (KAKAO_SNS_CLIENT_SECRET != null && !KAKAO_SNS_CLIENT_SECRET.isEmpty()) {
            params.add("client_secret", KAKAO_SNS_CLIENT_SECRET);
        }

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                    KAKAO_SNS_TOKEN_BASE_URL,
                    request,
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
            System.err.println("Error requesting Kakao access token: " + e.getMessage());
            if (e instanceof org.springframework.web.client.HttpClientErrorException) {
                org.springframework.web.client.HttpClientErrorException httpError
                        = (org.springframework.web.client.HttpClientErrorException) e;
                System.err.println("HTTP Status Code: " + httpError.getStatusCode());
                System.err.println("Response Body: " + httpError.getResponseBodyAsString());
            }
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        System.out.println("=== Kakao Get User Info ===");
        System.out.println("Access Token: " + (accessToken != null ? "EXISTS" : "NULL"));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    KAKAO_SNS_USERINFO_URL,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            System.out.println("User Info Response Status: " + response.getStatusCode());
            System.out.println("User Info Response Body: " + response.getBody());

            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                JsonNode kakaoAccount = jsonNode.get("kakao_account");
                JsonNode profile = kakaoAccount.get("profile");

                String providerId = jsonNode.get("id").asText();
                String name = profile.get("nickname").asText();
                String profileImage = profile.has("profile_image_url") ? profile.get("profile_image_url").asText() : null;

                System.out.println("Provider ID: " + providerId);
                System.out.println("Name: " + name);

                return OAuthUserInfo.builder()
                        .provider("KAKAO")
                        .providerId(providerId)
                        .email(null) // 카카오는 이메일 제공 안 함
                        .name(name)
                        .profileImage(profileImage)
                        .build();
            }
        } catch (Exception e) {
            System.err.println("Error getting Kakao user info: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public SocialLoginType type() {
        return SocialLoginType.KAKAO;
    }
}
