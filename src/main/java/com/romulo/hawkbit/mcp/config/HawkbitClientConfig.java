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

import org.eclipse.hawkbit.mgmt.rest.api.MgmtTargetRestApi;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class HawkbitClientConfig {

    @Value("${hawkbit.server.mgmt-url}")
    private String mgmtUrl;

    @Bean
    public HawkbitClient hawkbitClient(final HawkbitServer hawkbitServer, final Encoder encoder, final Decoder decoder,
            final Contract contract) {
        return new HawkbitClient(hawkbitServer, encoder, decoder, contract);
    }

    @Bean
    AuthenticationSetupHelper mgmtApi(final Tenant tenant, final HawkbitClient hawkbitClient) {
        return new AuthenticationSetupHelper(tenant, hawkbitClient);
    }

    @Bean
    public MgmtTargetRestApi mgmtTargetRestApi(
            final Encoder encoder,
            final Decoder decoder,
            final Contract contract,
            @org.springframework.beans.factory.annotation.Qualifier("oauth2FeignRequestInterceptor") final feign.RequestInterceptor oauth2FeignRequestInterceptor,
            final feign.Logger.Level logLevel) {

        return feign.Feign.builder()
                .encoder(encoder)
                .decoder(decoder)
                .contract(contract)
                .requestInterceptor(oauth2FeignRequestInterceptor)
                .errorDecoder(HawkbitClient.DEFAULT_ERROR_DECODER)
                .logger(new feign.Logger.JavaLogger(MgmtTargetRestApi.class))
                .logLevel(logLevel)
                .target(MgmtTargetRestApi.class, mgmtUrl);
    }

    @Bean
    feign.Logger.Level feignLoggerLevel() {
        return feign.Logger.Level.FULL;
    }
}
