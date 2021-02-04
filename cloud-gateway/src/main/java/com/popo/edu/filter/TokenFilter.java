package com.popo.edu.filter;

import com.popo.edu.request.TokenServiceFeignClient;
import io.netty.handler.codec.http.cookie.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class TokenFilter implements GlobalFilter, Ordered {

    private final TokenServiceFeignClient tokenServiceFeignClient;

    private static Set<String> noRequiredTokens = new HashSet<>();

    static {
        noRequiredTokens.add("/code/create");
        noRequiredTokens.add("/user/login/");
        noRequiredTokens.add("/user/register/");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String token = null;
        String path = request.getPath().toString();
        if (noRequiredTokens.stream().noneMatch(noRequiredToken -> path.contains(noRequiredToken))) {
            MultiValueMap<String, HttpCookie> cookies = request.getCookies();
            for (Map.Entry<String, List<HttpCookie>> stringListEntry : cookies.entrySet()) {
                if (stringListEntry.getKey().equals("token")) {
                    token = stringListEntry.getValue().get(0).getValue();
                    break;
                }
            }
            Assert.isTrue(token != null, "token is not exists");
            String email = tokenServiceFeignClient.queryEmailByToken(token);
            Assert.isTrue(email != null, "token is invaild");
        }


//        String requestEmail = exchange.getRequest().getQueryParams().getFirst("email");
//        Assert.isTrue(requestEmail == null || requestEmail.equals(email), "token is invaild");

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
