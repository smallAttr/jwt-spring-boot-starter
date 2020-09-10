package com.apricotforest.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.RequiredTypeException;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author smallAttr
 * @since 2020-08-27 17:46
 */
@Configuration
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private JwtTokenConfig config;

    Authentication getAuthentication(String secretKey, String token) {
        try {
            Claims claims = parseToken(secretKey, token);
            return new UsernamePasswordAuthenticationToken(claims, "", null);
        } catch (RequiredTypeException rte) {

            throw new IllegalArgumentException("操作员信息格式错误", rte);
        }
    }

    /**
     * token解析.
     */
    private Claims parseToken(String secretKey, String token) {
        Claims body = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return body;

    }

    String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    boolean validateToken(String secretKey, String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException expected) {

            logger.debug("jwt校验失败", expected);
            throw new IllegalArgumentException("请重新登录", expected);
        }
    }

    String generateToken(String secretKey, Map<String, Object> claims) {

        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.YEAR, 5);
        return Jwts.builder().addClaims(claims)
                .signWith(config.getAlgorithm(), secretKey)
                .setExpiration(instance.getTime()).compact();
    }

    @Autowired
    public JwtTokenProvider setConfig(JwtTokenConfig config) {
        this.config = config;
        return this;
    }
}
