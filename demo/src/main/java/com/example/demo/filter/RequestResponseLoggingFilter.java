package com.example.demo.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import reactor.core.publisher.Mono;

@Component
public class RequestResponseLoggingFilter implements GlobalFilter {

    private static final Logger log =
            LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        long startTime = System.currentTimeMillis();

        String method = exchange.getRequest().getMethod().name();
        String path = exchange.getRequest().getURI().getPath();

        log.info("[GATEWAY REQUEST] {} {}", method, path);

        return chain.filter(exchange).then(
                Mono.fromRunnable(() -> {

                    long duration =
                            System.currentTimeMillis() - startTime;

                    int status =
                            exchange.getResponse()
                                    .getStatusCode()
                                    .value();

                    log.info("[GATEWAY RESPONSE] {} ({} ms)",
                            status, duration);
                })
        );
    }
}