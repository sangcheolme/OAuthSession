package com.oauthsession.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.oauthsession.oauth2.CustomClientRegistrationRepo;
import com.oauthsession.service.CustomOAuth2UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomClientRegistrationRepo customClientRegistrationRepo;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable());

        http
            .formLogin(login -> login.disable());

        http
            .httpBasic(basic -> basic.disable());

        http
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .clientRegistrationRepository(customClientRegistrationRepo.clientRegistrationRepository())
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)));

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/oauth2/**", "/login/**", "/logout/**", "/error/**").permitAll()
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
