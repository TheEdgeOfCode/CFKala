package com.codefathers.cfkserver.controller;

import com.codefathers.cfkserver.exceptions.token.ExpiredTokenException;
import com.codefathers.cfkserver.exceptions.token.InvalidTokenException;
import com.codefathers.cfkserver.service.file.StorageService;
import com.codefathers.cfkserver.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.codefathers.cfkserver.utils.ErrorUtil.sendError;
import static com.codefathers.cfkserver.utils.TokenUtil.checkToken;

@RestController
public class FileController {
    @Autowired
    private StorageService storageService;

    @RequestMapping("/upload/user/profile")
    @PostMapping
    private void saveProfileUser(@RequestBody InputStreamResource resource,
                                 HttpServletRequest request, HttpServletResponse response) {
        try {
            if (checkToken(response, request)) {
                storageService.saveProfile(TokenUtil.getUsernameFromToken(request), resource);
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            sendError(response, HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @RequestMapping("/download/user/profile")
    @GetMapping
    private ResponseEntity<?> getProfileImage(HttpServletRequest request, HttpServletResponse response) {
        try {
            checkToken(response, request);
            ByteArrayResource resource = storageService.getProfile(TokenUtil.getUsernameFromToken(request));
            return ResponseEntity.ok().body(resource);
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
            return null;
        }
    }

    @RequestMapping("/download/user/profile/{username}")
    @GetMapping
    private ResponseEntity<?> getProfileImage(HttpServletRequest request, HttpServletResponse response, @PathVariable String username) {
        try {
            checkToken(response, request);
            ByteArrayResource resource = storageService.getProfile(username);
            return ResponseEntity.ok().body(resource);
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
            return null;
        }
    }

    @PostMapping
    @RequestMapping("/upload/product/{id}")
    private void UpdateProductPhoto(@RequestBody ByteArrayResource[] images,
                                    HttpServletRequest request, HttpServletResponse response, @PathVariable Integer id) {
        try {
            checkToken(response, request);
            storageService.saveProductImage(id, images);
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (IOException e) {
            sendError(response, HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping
    @RequestMapping("/download/product/{id}")
    private ResponseEntity<?> productPhoto(@PathVariable Integer id, HttpServletResponse response) {
        try {
            return ResponseEntity.ok(storageService.getProductImages(id));
        } catch (Exception e) {
            sendError(response, HttpStatus.CONFLICT, e.getMessage());
            return null;
        }
    }

    @GetMapping
    @RequestMapping("/download/product/{id}/main")
    private ResponseEntity<?> productMainPhoto(@PathVariable Integer id, HttpServletResponse response) {
        try {
            return ResponseEntity.ok(storageService.getProductMainImage(id));
        } catch (Exception e) {
            sendError(response, HttpStatus.CONFLICT, e.getMessage());
            return null;
        }
    }
}
