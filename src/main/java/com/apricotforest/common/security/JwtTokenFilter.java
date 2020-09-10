package com.apricotforest.common.security;

import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.util.*;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.*;

/**
 * @author smallAttr
 * @since 2020-08-27 17:44
 */
public class JwtTokenFilter implements Filter {

    private JwtTokenProvider jwtTokenProvider;

    private String secretKey;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider, String secretKey) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.secretKey = secretKey;
    }

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        String token = jwtTokenProvider.resolveToken(request);
        if (StringUtils.hasText(token)) {
            if (jwtTokenProvider.validateToken(secretKey, token)) {
                Authentication auth = jwtTokenProvider.getAuthentication(secretKey, token);
                SecurityContext securityContext = SecurityContextHolder.getContext();
                securityContext.setAuthentication(auth);
            }
        }
        filterChain.doFilter(req, res);
    }

    @Override
    public void destroy() {

    }
}
