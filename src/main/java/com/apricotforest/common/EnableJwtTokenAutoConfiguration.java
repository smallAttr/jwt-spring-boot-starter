package com.apricotforest.common;

import com.apricotforest.common.security.*;
import com.apricotforest.nice.common.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.*;
import org.springframework.http.*;
import org.springframework.security.access.*;
import org.springframework.security.config.annotation.method.configuration.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.http.*;
import org.springframework.security.web.authentication.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.*;

/**
 * spring security {@link Configuration}.
 */
@EnableWebSecurity
@ComponentScan(value = "com.apricotforest.common")
@Configuration
@ConditionalOnClass(WebSecurityConfigurerAdapter.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "security.jwt.token", name = "enable", matchIfMissing = true)
public class EnableJwtTokenAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(EnableJwtTokenAutoConfiguration.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public JwtTokenFilter createFilter(
            JwtTokenProvider jwtTokenProvider,
            JwtTokenConfig config) {

        logger.debug("构建jwt验证token filter ... {}>>{}", jwtTokenProvider, config.getSecretKey());
        return new JwtTokenFilter(jwtTokenProvider, config.getSecretKey());
    }

    @Configuration
    @Order(1)
    @EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
    static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        private JwtTokenFilter jwtTokenFilter;

        private JwtTokenConfig config;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            http.anonymous().disable()
                    .csrf().disable()
                    .antMatcher(config.getAntPattern())
                    .authorizeRequests().anyRequest().authenticated();

            http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
            http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {

                logger.error("认证信息校验失败", authException);
                response.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8).toString());
                response.setStatus(401);
                try (Writer writer = response.getWriter()) {

                    writer.write(objectMapper.writeValueAsString(Result.fail(401, "接口认证失败，请提供验证凭据")));
                }
            }).accessDeniedHandler((HttpServletRequest request, HttpServletResponse response,
                                    AccessDeniedException accessDeniedException) -> {

                response.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8).toString());
                response.setStatus(403);
                try (Writer writer = response.getWriter()) {
                    writer.write(objectMapper.writeValueAsString(Result.fail(403, "无权限访问该接口")));
                }
            });
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            String[] array = new String[config.getIgnoreAntPatterns().size()];
            web.ignoring().antMatchers(config.getIgnoreAntPatterns().toArray(array));
        }

        @Autowired
        public void setJwtTokenFilter(JwtTokenFilter jwtTokenFilter) {
            this.jwtTokenFilter = jwtTokenFilter;
        }

        @Autowired
        public void setConfig(JwtTokenConfig config) {
            this.config = config;
        }
    }


}
