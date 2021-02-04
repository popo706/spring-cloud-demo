package com.popo.edu.request;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "lagou-service-user",path = "/user")
public interface TokenServiceFeignClient {

    /**
     * 根据token获取邮箱地址
     *
     * @param token 令牌
     * @return 邮箱地址
     */
    @GetMapping("/info/{token}")
    String queryEmailByToken(@PathVariable("token") String token);
}
