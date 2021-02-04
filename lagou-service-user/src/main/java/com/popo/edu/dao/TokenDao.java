package com.popo.edu.dao;

import com.popo.edu.pojo.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenDao extends JpaRepository<Token,Long> {

    Token findOneByToken(String token);

    Token findOneByEmail(String email);
}
