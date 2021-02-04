package com.popo.edu.request;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "lagou-service-code",path = "/code")
public interface CodeServiceFeignClient {

    /**
     * 校验验证码是否正确，0正确1错误2超时
     *
     * @param email 邮箱地址
     * @param code  验证码
     * @return 状态
     */
    @GetMapping("/validate/{email}/{code}")
    Long checkCode(@PathVariable("email") String email, @PathVariable("code") String code);
}
