package com.popo.edu.controller;

import com.popo.edu.dao.TokenDao;
import com.popo.edu.dao.UserDao;
import com.popo.edu.pojo.Token;
import com.popo.edu.pojo.User;
import com.popo.edu.request.CodeServiceFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserDao userDao;
    private final TokenDao tokenDao;
    private final CodeServiceFeignClient codeServiceFeignClient;

    /**
     * 根据邮箱地址查询用户是否存在
     *
     * @param email 邮箱地址
     * @return 是否存在
     */
    @GetMapping("/isRegistered/{email}")
    @ResponseStatus(HttpStatus.OK)
    public boolean existsByEmail(@PathVariable String email) {
        return userDao.findOneByEmail(email) != null;
    }

    /**
     * 登陆
     * 登录接⼝口，验证⽤用户名密码合法性，根据⽤用户名和 密码⽣生成token，token存⼊入数据库，
     * 并写⼊入cookie 中，登录成功返回邮箱地址，重定向到欢迎⻚页
     *
     * @param email    邮箱地址
     * @param password 密码
     */
    @GetMapping("/login/{email}/{password}")
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public String login(@PathVariable("email") String email, @PathVariable("password") String password, HttpServletResponse response) {
        User user = userDao.findOneByEmail(email);
        if(user==null || !user.getPassword().equals(password)){
            return "false";
        }
        Token token = tokenDao.findOneByEmail(email);
        if (token == null) {
            token = new Token(email);
            tokenDao.save(token);
        }
        Cookie cookie = new Cookie("token", token.getToken());
        cookie.setPath("/");
        cookie.setMaxAge(10000);
        response.addCookie(cookie);
        return "true";
    }

    /**
     * 根据token获取邮箱地址
     *
     * @param token 令牌
     * @return 邮箱地址
     */
    @GetMapping("/info/{token}")
    @ResponseBody
    public String queryEmailByToken(@PathVariable String token) {
        Token originToken = tokenDao.findOneByToken(token);
        String email = originToken != null ? originToken.getEmail() : null;
        return email;
    }

    /**
     * 注册接口
     *
     * @param email    邮箱地址
     * @param password 密码
     * @param code     验证码
     * @return 是否注册成功
     */
    @GetMapping("/register/{email}/{password}/{code}")
    @Transactional
    public Long register(@PathVariable("email") String email, @PathVariable("password") String password, @PathVariable("code") String code) {
        try {
            User user = userDao.findOneByEmail(email);
            if(user!=null){
                return 1L;
            }

            //0正确1错误2超时
            Long status = codeServiceFeignClient.checkCode(email, code);
            if (status != 0) {
                return status;
            }
            //注册
            userDao.save(new User(email, password));
            return status;
        } catch (Exception e) {
            log.error("注册失败,email:{},password:{},code:{},error:{}", email, password, code, e);
            return 1L;
        }
    }
}
