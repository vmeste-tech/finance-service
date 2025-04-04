package ru.kolpakovee.finance_service.interceptors;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import ru.kolpakovee.finance_service.constants.AuthConstants;

@Slf4j
public class JwtRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt oauthToken = (Jwt) authentication.getCredentials();
        requestTemplate.header(HttpHeaders.AUTHORIZATION, AuthConstants.BEARER + oauthToken.getTokenValue());
    }
}
