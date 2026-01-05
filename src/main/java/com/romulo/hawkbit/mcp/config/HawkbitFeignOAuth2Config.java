package com.romulo.hawkbit.mcp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import feign.RequestInterceptor;

@Configuration
public class HawkbitFeignOAuth2Config {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(HawkbitFeignOAuth2Config.class);

    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor(
            OAuth2AuthorizedClientManager authorizedClientManager) {

        return requestTemplate -> {
            OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("keycloak")
                    .principal("hawkbit-mcp-server")
                    .build();

            OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);

            if (authorizedClient == null) {
                throw new IllegalStateException("Não foi possível obter o token OAuth2");
            }

            OAuth2AccessToken accessToken = authorizedClient.getAccessToken();

            log.debug(">>> INTERCEPTOR: Injecting Authorization header with dynamic token");

            requestTemplate.header(
                    "Authorization",
                    "Bearer " + accessToken.getTokenValue());
        };
    }
}