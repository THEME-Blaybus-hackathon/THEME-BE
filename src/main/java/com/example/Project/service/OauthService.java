package com.example.Project.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Project.dto.OAuthUserInfo;
import com.example.Project.helper.constants.SocialLoginType;
import com.example.Project.service.social.SocialOauth;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final List<SocialOauth> socialOauthList;

    public String getRedirectUrl(SocialLoginType socialLoginType) {
        SocialOauth socialOauth = this.findSocialOauthByType(socialLoginType);
        return socialOauth.getOauthRedirectURL();
    }

    public String requestAccessToken(SocialLoginType socialLoginType, String code) {
        SocialOauth socialOauth = this.findSocialOauthByType(socialLoginType);
        return socialOauth.requestAccessToken(code);
    }

    public OAuthUserInfo getUserInfo(SocialLoginType socialLoginType, String accessToken) {
        SocialOauth socialOauth = this.findSocialOauthByType(socialLoginType);
        return socialOauth.getUserInfo(accessToken);
    }

    private SocialOauth findSocialOauthByType(SocialLoginType socialLoginType) {
        return socialOauthList.stream()
                .filter(x -> x.type() == socialLoginType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알 수 없는 SocialLoginType 입니다."));
    }
}
