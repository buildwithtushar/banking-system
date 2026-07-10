package com.bank.api_gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class BankingRequestLoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        String RequestId = UUID.randomUUID().toString();
        exchange.getResponse().getHeaders().add("X-Request-id",RequestId);
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();
        String user = exchange.getRequest().getHeaders().getFirst("X-User-Id");
        log.info("[REQ] id={} user={} {} {}",RequestId, user, method, path);
        return chain.filter(exchange).doOnSuccess(v->{
            long timeTaken = System.currentTimeMillis()-startTime;
            log.info("[Res] id = {} status={} time={}ms",RequestId,exchange.getResponse().getStatusCode(),timeTaken);
        }).doOnError(ex ->{
            long timeTaken = System.currentTimeMillis()-startTime;
            log.error("[err] id={} {} {} time={}ms error={}",RequestId,method,path,timeTaken);
        });
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
