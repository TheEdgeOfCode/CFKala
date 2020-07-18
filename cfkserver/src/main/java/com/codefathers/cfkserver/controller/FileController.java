package com.codefathers.cfkserver.controller;

import com.codefathers.cfkserver.exceptions.token.ExpiredTokenException;
import com.codefathers.cfkserver.exceptions.token.InvalidTokenException;
import com.codefathers.cfkserver.service.file.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @RequestMapping("/upload/profile_photo/{username}")
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
}
