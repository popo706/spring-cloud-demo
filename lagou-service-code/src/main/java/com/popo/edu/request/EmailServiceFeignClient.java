package com.popo.edu.request;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "lagou-service-email")
@RequestMapping("email")
public interface EmailServiceFeignClient {

    /**
     * 调用邮件微服务推送邮件
     *
     * @param email 邮件地址
     * @param code  验证码
     * @return 是否成功
     */
    @GetMapping("/{email}/{code}")
    boolean sendEmail(@PathVariable("email") String email, @PathVariable("code") String code);
}
