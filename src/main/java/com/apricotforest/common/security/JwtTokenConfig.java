package com.apricotforest.common.security;

import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.boot.context.properties.*;

import java.util.*;

/**
 * @author smallAttr
 * @since 2020-08-27 17:47
 */
@ConfigurationProperties(prefix = "security.jwt.token.config")
public class JwtTokenConfig {

    /**
     * 安全认证key
     */
    private String secretKey;

    /**
     * 需认证的url(默认拦截所有请求)
     */
    private String antPattern = "/**";

    /**
     * 不需要认证的url
     */
    private List<String> ignoreAntPatterns = new ArrayList<>();

    /**
     * jwt签名算法
     */
    private SignatureAlgorithm algorithm = SignatureAlgorithm.HS256;

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getAntPattern() {
        return antPattern;
    }

    public void setAntPattern(String antPattern) {
        this.antPattern = antPattern;
    }

    public SignatureAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(SignatureAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public List<String> getIgnoreAntPatterns() {
        return ignoreAntPatterns;
    }

    public void setIgnoreAntPatterns(List<String> ignoreAntPatterns) {
        this.ignoreAntPatterns.add("/resources/**");
        this.ignoreAntPatterns.add("/static/**");
        this.ignoreAntPatterns.addAll(ignoreAntPatterns);
    }
}
