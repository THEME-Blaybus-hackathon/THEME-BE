package com.example.Project.service.social;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
public class NaverOauth implements SocialOauth {

    @Value("${sns.naver.url}")
    private String NAVER_SNS_BASE_URL;
    @Value("${sns.naver.client.id}")
    private String NAVER_SNS_CLIENT_ID;
    @Value("${sns.naver.client.secret}")
    private String NAVER_SNS_CLIENT_SECRET;
    @Value("${sns.naver.callback.url}")
    private String NAVER_SNS_CALLBACK_URL;
    @Value("${sns.naver.token.url}")
    private String NAVER_SNS_TOKEN_BASE_URL;
    @Value("${sns.naver.userinfo.url}")
    private String NAVER_SNS_USERINFO_URL;

    @Override
    public String getOauthRedirectURL() {
        Map<String, Object> params = new HashMap<>();
        params.put("response_type", "code");
        params.put("client_id", NAVER_SNS_CLIENT_ID);
        params.put("redirect_uri", NAVER_SNS_CALLBACK_URL);
        params.put("state", "RANDOM_STATE");

        String parameterString = params.entrySet().stream()
                .map(x -> x.getKey() + "=" + x.getValue())
                .collect(Collectors.joining("&"));

        return NAVER_SNS_BASE_URL + "?" + parameterString;
    }

    @Override
    public String requestAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> params = new HashMap<>();
        params.put("grant_type", "authorization_code");
        params.put("client_id", NAVER_SNS_CLIENT_ID);
        params.put("client_secret", NAVER_SNS_CLIENT_SECRET);
        params.put("code", code);
        params.put("state", "RANDOM_STATE");

        String parameterString = params.entrySet().stream()
                .map(x -> x.getKey() + "=" + x.getValue())
                .collect(Collectors.joining("&"));

        String url = NAVER_SNS_TOKEN_BASE_URL + "?" + parameterString;

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
                return jsonNode.get("access_token").asText();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                NAVER_SNS_USERINFO_URL,
                HttpMethod.GET,
                entity,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                JsonNode responseNode = jsonNode.get("response");

                return OAuthUserInfo.builder()
                        .provider("NAVER")
                        .providerId(responseNode.get("id").asText())
                        .email(responseNode.has("email") ? responseNode.get("email").asText() : null)
                        .name(responseNode.has("name") ? responseNode.get("name").asText() : responseNode.get("nickname").asText())
                        .profileImage(responseNode.has("profile_image") ? responseNode.get("profile_image").asText() : null)
                        .build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public SocialLoginType type() {
        return SocialLoginType.NAVER;
    }
}
