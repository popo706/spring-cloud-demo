package com.popo.edu.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.Random;

@Data
@Entity
@Table(name = "lagou_auth_code")
@NoArgsConstructor
public class Code {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 过期时间
     */
    private Date expiretime;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 验证码
     */
    private String code;

    public Code(String email) {
        this.createTime = new Date();
        this.expiretime = new Date(this.createTime.getTime() + 600000L);
        this.email = email;
        this.code = randomString();
    }

    /**
     * 随机生成一个字符
     */
    private static String randomString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(new Random().nextInt(10));
        }
        return sb.toString();
    }
}
