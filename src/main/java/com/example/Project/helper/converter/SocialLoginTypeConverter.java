package com.example.Project.helper.converter;

import org.springframework.core.convert.converter.Converter;

import com.example.Project.helper.constants.SocialLoginType;

public class SocialLoginTypeConverter implements Converter<String, SocialLoginType> {

    @Override
    public SocialLoginType convert(String s) {
        return SocialLoginType.valueOf(s.toUpperCase());
    }
}
