package com.nls.apigateway.config;

import com.nls.apigateway.filter.JWTAuthenticationFilter;
import com.nls.common.enumration.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTAuthenticationFilter jwtAuthFilter;

    private static final String[] PUBLIC_ENDPOINT = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/webjars/**",
            "/user-service/api/auth/**",
            "/",
            "/user-service/api/v3/api-docs",
            "/booking-service/api/v3/api-docs",
            "/payment-service/api/v3/api-docs",
            "/notification-service/api/v3/api-docs",
            "/recommendation-service/api/v3/api-docs",
            "/payment-service/api/payment/IPN/vnpay",
            "/user-service/api/user/confirm",
            "/booking-service/api/property",
            "/booking-service/api/property/{propertyId}"
    };

    private static final String[] USER_ENDPOINT = {
            "/user-service/api/user",
            "/booking-service/api/booking",
            "/booking-service/api/property/add",
            "/booking-service/api/property/update",

    };


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(PUBLIC_ENDPOINT).permitAll()
                        .pathMatchers(USER_ENDPOINT).hasAuthority(Role.USER.name())
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
        corsConfig.addAllowedOrigin("*");
        corsConfig.addAllowedHeader("*");
        corsConfig.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

}
