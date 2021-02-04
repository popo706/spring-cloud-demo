package com.popo.edu.dao;

import com.popo.edu.pojo.Code;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface CodeDao extends JpaRepository<Code, Long> {

    /**
     * 根据邮箱地址查询最新一条有效的验证码
     *
     * @param email 邮箱地址
     * @param date  时间
     * @return 验证码
     */
    Code findTopByEmailAndExpiretimeAfterOrderByExpiretimeDesc(String email, Date date);

    /**
     * 根据邮箱地址查询最新一条有效的验证码
     *
     * @param email 邮箱地址
     * @return 验证码
     */
    Code findTopByEmailOrderByExpiretimeDesc(String email);
}
