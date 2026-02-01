package com.example.Project.service.social;

import com.example.Project.dto.OAuthUserInfo;
import com.example.Project.helper.constants.SocialLoginType;

public interface SocialOauth {

    String getOauthRedirectURL();

    String requestAccessToken(String code);

    OAuthUserInfo getUserInfo(String accessToken);

    default SocialLoginType type() {
        if (this instanceof GoogleOauth) {
            return SocialLoginType.GOOGLE;
        } else if (this instanceof KakaoOauth) {
            return SocialLoginType.KAKAO;
        } else if (this instanceof NaverOauth) {
            return SocialLoginType.NAVER;
        } else {
            return null;
        }
    }
}
