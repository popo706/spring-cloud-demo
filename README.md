# spring-cloud-demo
spring-cloud-demo 是一个基于Spring Cloud 框架的登陆注册demo项目.

**业务描述**

以注册、登录为主线，串串联起验证码⽣生成及校验、邮件发送、IP防暴暴刷、⽤用户统⼀一认证等功能。实现需 基于Spring Cloud 微服务架构，技术涉及Nginx、Eureka、Feign(Ribbon、Hystrix)、Gateway、 Config+Bus等。

### 架构图
![Image text](https://raw.githubusercontent.com/hongmaju/light7Local/master/img/productShow/20170518152848.png)

### 注册

1) ⽤用户访问到登录⻚页⾯面，在登录⻚页⾯面中有注册新账号功能 
2) 点击“注册新账号“，跳转到注册⻚页⾯面
3) 在注册⻚页⾯面，需要⽤用户输⼊入邮箱地址、密码、确认密码，然后点击”获取验证码“，系统会⽣生成验证码 并向所输⼊入的邮箱地址发送该验证码，⽤用户拿到邮箱中的验证码输⼊入后完成注册

### 实现功能点 

* 注册新账号
* 一分钟内只允许获取一次验证码
* 发邮件功能
* 校验验证码
* 验证码超时展示
* 保存令牌数据库
* 令牌保存cookie中
* 跳转到欢迎页面
* 登录
* 生成Token保存到令牌表和Cookies中最后转到欢迎页面
* 未登录状态网关拦截
* 回IP防暴刷过滤器
* 在1分钟内注册超过100次时返回错误信息

### 其他配置信息
#### host文件配置
````
// eureka server注册中心服务名称
127.0.0.1 CloudEurekaServerA
127.0.0.1 CloudEurekaServerB

// 和 nginx 一起解决跨域问题
127.0.0.1 www.test.com
````

#### nginx 配置
```
server {
        listen       80;
        server_name www.test.com;
        
        #动静分离，静态资源 source目录下 static文件夹放到 nginx主目录 html文件下
        location /static/ {
                  root html;
        }
        
        #gateway请求转发配置
        location /api/ {
                  proxy_pass http://127.0.0.1:9002/;
        }
}
```

功能验证直接访问 www.test.com/login.html 就可以了（注：如果nginx 非 80 端口，可能需要添加port,如 www.test.com:10001/login.html）
