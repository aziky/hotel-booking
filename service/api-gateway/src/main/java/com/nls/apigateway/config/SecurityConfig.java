package com.nls.apigateway.config;

import com.nls.apigateway.filter.JWTAuthenticationFilter;
import com.nls.apigateway.properties.WebUrlProperties;
import com.nls.common.enumration.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTAuthenticationFilter jwtAuthFilter;
    private final WebUrlProperties webUrlProperties;

    private static final String USER_PREFIX = "/user-service/api";
    private static final String BOOKING_PREFIX = "/booking-service/api";
    private static final String PAYMENT_PREFIX = "/payment-service/api";

    private static final String[] PUBLIC_ENDPOINT = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/webjars/**",
            "/",
            "/user-service/api/v3/api-docs",
            "/booking-service/api/v3/api-docs",
            "/payment-service/api/v3/api-docs",
            "/notification-service/api/v3/api-docs",
            "/recommendation-service/api/v3/api-docs",
            USER_PREFIX + "/auth/**",
            USER_PREFIX + "/user/confirm",
            USER_PREFIX + "/user/forget-password",
            USER_PREFIX + "/user/reset-password",
            BOOKING_PREFIX + "/property",
            BOOKING_PREFIX + "/property",
            BOOKING_PREFIX + "/property/{propertyId}",
            PAYMENT_PREFIX + "/payment/IPN/vnpay",
            PAYMENT_PREFIX + "/payment/IPN/payos",
            BOOKING_PREFIX + "/admin/dashboard",
            USER_PREFIX + "/review/property/{propertyId}",
    };

    private static final String[] USER_ENDPOINT = {
            USER_PREFIX + "/user",
            BOOKING_PREFIX + "/booking",
            BOOKING_PREFIX + "/property/add",  //test
            BOOKING_PREFIX + "/property/update", //test
            USER_PREFIX + "/user/change-role",
            BOOKING_PREFIX + "/booking/my-bookings",
            USER_PREFIX + "/user/profile",

    };
    private static final String[] HOST_ENDPOINT = {
            BOOKING_PREFIX + "/property/add",  //test
            BOOKING_PREFIX + "/property/update"
    };

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(PUBLIC_ENDPOINT).permitAll()
                        .pathMatchers(USER_ENDPOINT).hasAuthority(Role.USER.name())
                        .pathMatchers(HOST_ENDPOINT).hasAuthority(Role.HOST.name())
                        .anyExchange().authenticated()
                )
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }



    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(true);
        corsConfig.setAllowedOrigins(List.of(
                webUrlProperties.host(),
                "http://localhost:8050"
        ));
        corsConfig.addAllowedHeader("*");
        corsConfig.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

}
