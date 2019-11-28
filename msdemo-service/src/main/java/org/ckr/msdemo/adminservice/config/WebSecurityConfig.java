package org.ckr.msdemo.adminservice.config;

import org.ckr.msdemo.adminservice.dao.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {



    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {


        http
            .csrf().disable()
            .authorizeRequests()
            //for swagger UI, just by pass security checking.
                .antMatchers("/swagger-ui.html",
                    "/swagger-resources",
                    "/swagger-resources/**/*",
                    "/webjars/**/*",
                    "/v2/api-docs").permitAll()
                //login url should not be controlled
                .antMatchers("/login").permitAll()
                .anyRequest()
                    .authenticated()
                    .and()
                    .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
                    ;




    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomizedAuthenticationEntryPoint();
    }



    public static class CustomizedAuthenticationEntryPoint implements AuthenticationEntryPoint {
        private static final Logger LOG = LoggerFactory.getLogger(CustomizedAuthenticationEntryPoint.class);

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            LOG.error("Authentication is needed. Return status code " + HttpServletResponse.SC_UNAUTHORIZED);

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }


}
