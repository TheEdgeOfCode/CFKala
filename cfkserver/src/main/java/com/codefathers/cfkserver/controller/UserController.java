package com.codefathers.cfkserver.controller;

import com.codefathers.cfkserver.model.dtos.user.LoginDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @PostMapping
    private ResponseEntity<String> login(@RequestBody LoginDto dto) {
        return null;
    }
}
