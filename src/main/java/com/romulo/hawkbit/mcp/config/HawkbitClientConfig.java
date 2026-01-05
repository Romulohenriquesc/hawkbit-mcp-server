package com.romulo.hawkbit.mcp.config;

import org.eclipse.hawkbit.sdk.HawkbitClient;
import org.eclipse.hawkbit.sdk.HawkbitServer;
import org.eclipse.hawkbit.sdk.Tenant;
import org.eclipse.hawkbit.sdk.mgmt.AuthenticationSetupHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Contract;
import feign.codec.Decoder;
import feign.codec.Encoder;

import org.springframework.beans.factory.annotation.Value;

@Configuration
public class HawkbitClientConfig {

    @Value("${hawkbit.server.mgmt-url}")
    private String mgmtUrl;

    @Bean
    public HawkbitClient hawkbitClient(
            final HawkbitServer hawkbitServer,
            final Encoder encoder,
            final Decoder decoder,
            final Contract contract,
            @org.springframework.beans.factory.annotation.Qualifier("oauth2FeignRequestInterceptor") final feign.RequestInterceptor oauth2FeignRequestInterceptor) {

        return new HawkbitClient(
                hawkbitServer,
                encoder,
                decoder,
                contract,
                HawkbitClient.DEFAULT_ERROR_DECODER,
                (tenant, controller) -> oauth2FeignRequestInterceptor);
    }

    @Bean
    AuthenticationSetupHelper mgmtApi(final Tenant tenant, final HawkbitClient hawkbitClient) {
        return new AuthenticationSetupHelper(tenant, hawkbitClient);
    }

    @Bean
    feign.Logger.Level feignLoggerLevel() {
        return feign.Logger.Level.FULL;
    }
}
