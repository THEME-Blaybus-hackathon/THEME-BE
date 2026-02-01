package com.example.Project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuthUserInfo {

    private String provider;        // google, kakao, naver
    private String providerId;      // 제공자의 사용자 ID
    private String email;
    private String name;
    private String profileImage;
}
