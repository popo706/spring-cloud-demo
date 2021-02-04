package com.popo.edu.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IPAntiBrushFilter implements GlobalFilter, Ordered {

    private static Map<String, Set<Long>> ipRegisterMap = new ConcurrentHashMap<>();

    /**
     * 时间范围
     */
    @Value("${ip.limit.timeInMilliseconds}")
    private Long timeInMilliseconds;

    /**
     * 最大请求注册次数
     */
    @Value("${ip.limit.maxRegisterTimes}")
    private Long maxRegisterTimes;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String path = request.getPath().toString();
        if (path.contains("/register/")) {
            Set<Long> requestTimes;
            String clientIp = request.getRemoteAddress().getHostString();
            if (ipRegisterMap.containsKey(clientIp)) {
                requestTimes = ipRegisterMap.get(clientIp);
                filter(requestTimes);
                if (requestTimes.size() >= maxRegisterTimes) {
                    response.setStatusCode(HttpStatus.FORBIDDEN); // 状态码
                    String data = "您频繁进⾏行行注册，请求已被拒绝";
                    DataBuffer wrap = response.bufferFactory().wrap(data.getBytes());
                    return response.writeWith(Mono.just(wrap));
                }
            } else {
                requestTimes = new HashSet<>();
                ipRegisterMap.put(clientIp, requestTimes);
            }
            requestTimes.add(System.currentTimeMillis());
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private void filter(Set<Long> requestTimes) {
        Long beforeTime = System.currentTimeMillis() - timeInMilliseconds;
        requestTimes.removeIf(requestTime -> requestTime < beforeTime);
    }
}
