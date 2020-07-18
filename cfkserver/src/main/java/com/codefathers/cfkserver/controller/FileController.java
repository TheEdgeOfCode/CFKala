package com.codefathers.cfkserver.controller;

import com.codefathers.cfkserver.exceptions.token.ExpiredTokenException;
import com.codefathers.cfkserver.exceptions.token.InvalidTokenException;
import com.codefathers.cfkserver.service.file.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.codefathers.cfkserver.utils.ErrorUtil.sendError;
import static com.codefathers.cfkserver.utils.TokenUtil.checkToken;

@RestController
public class FileController {
    @Autowired
    private StorageService storageService;

    @RequestMapping("/upload/user/profile/{username}")
    @PostMapping
    private void saveProfileUser(@PathVariable String username, @RequestBody MultipartFile file,
                                 HttpServletRequest request, HttpServletResponse response) {
        try {
            if (checkToken(response, request)) {
                storageService.saveProfile(username, file);
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            sendError(response, HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @RequestMapping("/download/user/profile/{username}")
    @GetMapping
    private ResponseEntity<?> getProfileImage(@PathVariable String username,
                                              HttpServletRequest request, HttpServletResponse response) {
        try {
            checkToken(response, request);
            Resource resource = storageService.getProfile(username);
            return ResponseEntity.ok().body(resource);
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
            return null;
        }
    }
}
