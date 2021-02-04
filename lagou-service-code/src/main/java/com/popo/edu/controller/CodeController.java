package com.popo.edu.controller;

import com.popo.edu.dao.CodeDao;
import com.popo.edu.pojo.Code;
import com.popo.edu.request.EmailServiceFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/code")
public class CodeController {

    private final CodeDao codeDao;
    private final EmailServiceFeignClient emailServiceFeignClient;

    /**
     * ⽣生成验证码并发送到对应邮箱，成功true，失败 false
     */
    @GetMapping("/create/{email}")
    @Transactional
    public boolean generateCode(@PathVariable("email") String email) {
        try {
            Code code = codeDao.save(new Code(email));
            boolean sucess = emailServiceFeignClient.sendEmail(email, code.getCode());
            //sucess=false ,code 数据回滚
            return sucess;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 校验验证码是否正确，0正确1错误2超时
     *
     * @param email 邮箱地址
     * @param code  验证码
     * @return 状态
     */
    @GetMapping("/validate/{email}/{code}")
    public Long checkCode(@PathVariable("email") String email, @PathVariable("code") String code) {
        Code originCode = codeDao.findTopByEmailOrderByExpiretimeDesc(email);
        if (originCode == null || !originCode.getCode().equals(code)) {
            return 1L;
        }
        if (originCode.getExpiretime().before(new Date())) {
            return 2L;
        }
        return 0L;
    }
}
