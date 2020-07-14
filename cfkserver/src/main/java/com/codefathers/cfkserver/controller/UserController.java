package com.codefathers.cfkserver.controller;

import com.codefathers.cfkserver.exceptions.model.user.NotVerifiedSeller;
import com.codefathers.cfkserver.exceptions.model.user.UserNotFoundException;
import com.codefathers.cfkserver.exceptions.model.user.WrongPasswordException;
import com.codefathers.cfkserver.model.dtos.user.LoginDto;
import com.codefathers.cfkserver.model.dtos.user.TokenRoleDto;
import com.codefathers.cfkserver.service.UserService;
import com.codefathers.cfkserver.utils.ErrorUtil;
import com.codefathers.cfkserver.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

import static com.codefathers.cfkserver.utils.ErrorUtil.sendError;
import static org.springframework.http.HttpStatus.*;

@RestController
public class UserController {
    private HashMap<String, String> tokens;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    private ResponseEntity<?> login(@RequestBody LoginDto dto, HttpServletResponse response) {
        try {
            String role = userService.login(dto.getUsername(), dto.getPassword());
            String token = jwtUtil.generateToken(dto.getUsername());
            return ResponseEntity.ok(new TokenRoleDto(token, role));
        } catch (UserNotFoundException | NotVerifiedSeller | WrongPasswordException e) {
            sendError(response, BAD_REQUEST, e.getMessage());
            return null;
        }
    }
}
