package com.apricotforest.common.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.context.*;

/**
 * @author smallAttr
 * @since 2020-08-28 10:42
 */
public class ServicePrincipalProvider {

    public static Claims getCurrentPrincipal() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return (Claims) securityContext.getAuthentication().getPrincipal();
    }

    public static <T> T getAttribute(String attributeName, Class<T> tClass) {
        Claims principal = getCurrentPrincipal();
        return principal.get(attributeName, tClass);
    }
}
