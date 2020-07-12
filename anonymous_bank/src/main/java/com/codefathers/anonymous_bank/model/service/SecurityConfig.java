package com.codefathers.anonymous_bank.model.service;

import com.codefathers.anonymous_bank.model.exceptions.token.ExpiredTokenException;
import com.codefathers.anonymous_bank.model.exceptions.token.InvalidTokenException;
import com.codefathers.anonymous_bank.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@EnableWebSecurity
@Component
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailService userdetailService;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserDetailService userDetailService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userdetailService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests().anyRequest().permitAll()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    private void check(HttpServletRequest request)
            throws ExpiredTokenException, InvalidTokenException {
        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("anonymous ")){
            jwt = authorizationHeader.substring(10);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                if (e instanceof ExpiredJwtException){
                    throw new ExpiredTokenException("Expired Token");
                }else {
                    throw new InvalidTokenException("Invalid Token");
                }
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailService.loadUserByUsername(username);
            if (jwtUtil.validateToken(jwt,userDetails)){

                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                        userDetails,null,userDetails.getAuthorities());
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(token);
            }
        }
    }

    public boolean validate(HttpServletResponse response,HttpServletRequest request){
        try {
            check(request);
            return true;
        } catch (Exception e) {
            response.resetBuffer();
            response.setStatus(401);
            response.setHeader("Content-Type", "application/json");
            try {
                response.getOutputStream().print("{\"errorMessage\":\"" + e.getMessage() + "\"}");
                response.flushBuffer();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }
}
