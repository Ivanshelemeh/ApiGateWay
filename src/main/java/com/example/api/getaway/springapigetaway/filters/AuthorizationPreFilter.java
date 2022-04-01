package com.example.api.getaway.springapigetaway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * @author shele
 * This is filter which starts to act
 * when request has been come
 */
@Component
public class AuthorizationPreFilter implements GlobalFilter {

    final Logger logger = LoggerFactory.getLogger(AuthorizationPreFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("pre filter authorize starts..");

        String reqHeader = exchange.getRequest().getPath().toString();
        HttpHeaders headers = exchange.getRequest().getHeaders();
        Set<String> keyHeaders = headers.keySet();
        keyHeaders.forEach((keyHeader) -> {
            String headValue = headers.getFirst(keyHeader);
        });
        return chain.filter(exchange);
    }
}
