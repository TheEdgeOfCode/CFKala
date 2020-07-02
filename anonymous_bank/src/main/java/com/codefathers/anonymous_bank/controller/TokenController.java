package com.codefathers.anonymous_bank.controller;

import com.codefathers.anonymous_bank.model.dtos.AuthenticationRequestDTO;
import com.codefathers.anonymous_bank.model.dtos.AuthenticationResponseDTO;
import com.codefathers.anonymous_bank.model.exceptions.token.UserPassInvalidException;
import com.codefathers.anonymous_bank.model.service.UserDetailService;
import com.codefathers.anonymous_bank.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailService userDetailservice;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/auth/get_token")
    private ResponseEntity<?> getToken(@RequestBody AuthenticationRequestDTO dto) throws Exception{
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getUsername(),dto.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new UserPassInvalidException("Incorrect Username Or Password",e);
        }
        final UserDetails userDetails = userDetailservice.loadUserByUsername(dto.getUsername());
        final String jwtToken = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponseDTO(jwtToken));
    }

    @RequestMapping("/auth/test")
    private String testToken(){
        return "Hello There !";
    }
}
