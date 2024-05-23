package com.oauthsession.service;

import java.util.Map;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GoogleOAuth2Response implements OAuth2Response {

    private final Map<String, Object> attributes;

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return attributes.get("sub").toString();
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }
}
