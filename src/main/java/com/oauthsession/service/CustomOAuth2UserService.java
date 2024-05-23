package com.oauthsession.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oauthsession.domain.Customer;
import com.oauthsession.domain.Role;
import com.oauthsession.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final CustomerRepository customerRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("OAuth2User: {}", oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("registrationId: {}", registrationId);

        OAuth2Response oAuth2Response = null;
        if ("naver".equals(registrationId)) {
            log.info("Naver User");
            oAuth2Response = new NaverOAuth2Response(oAuth2User.getAttributes());
        } else if ("google".equals(registrationId)) {
            log.info("Google User");
            oAuth2Response = new GoogleOAuth2Response(oAuth2User.getAttributes());
        } else {
            throw new OAuth2AuthenticationException("Unsupported OAuth2 Provider");
        }

        String username = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();
        Role roleUser = Role.ROLE_USER;

        Customer customer = customerRepository.findByUsername(username).orElse(null);

        if (customer == null) {
            log.info("New User");
            customer = Customer.builder()
                .username(username)
                .name(oAuth2Response.getName())
                .role(roleUser)
                .build();
            customerRepository.save(customer);
        } else {
            log.info("Existing User");
            customer.update(oAuth2Response.getName());
        }

        return new CustomOAuth2User(oAuth2Response, roleUser.name());
    }
}
