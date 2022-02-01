package com.example.api.getaway.springapigetaway.filters;

import io.jsonwebtoken.Jwts;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.ConfigFilter> {

    @Autowired
    Environment environment;

    public AuthorizationHeaderFilter() {
        super(ConfigFilter.class);
    }

    @Override
    public GatewayFilter apply(ConfigFilter config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No headers", HttpStatus.UNAUTHORIZED);
            }
            String authHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authHeader.replace("Bearer", "");

            if (!isJwtValid(jwt)) {
                return onError(exchange, "Not valid token", HttpStatus.UNAUTHORIZED);
            }
            return chain.filter(exchange);
        });
    }

    public Mono<Void> onError(ServerWebExchange exchange, String error, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }

    public static class ConfigFilter {
        //Put some code
    }

    private boolean isJwtValid(String jwt) {
        boolean value = true;
        String jwtSubject = null;
        try {
            jwtSubject = Jwts.parser().setSigningKey(environment.getProperty("token.secret"))
                    .parseClaimsJws(jwt)
                    .getBody()
                    .getSubject();
        } catch (Exception ex) {
            value = false;
        }

        if (jwtSubject == null || jwtSubject.isEmpty()) {
            value = false;
        }

        return value;
    }
}
