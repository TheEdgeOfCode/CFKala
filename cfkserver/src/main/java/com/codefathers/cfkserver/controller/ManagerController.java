package com.codefathers.cfkserver.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.codefathers.cfkserver.utils.ErrorUtil.sendError;
import static com.codefathers.cfkserver.utils.TokenUtil.checkToken;

@RestController
public class ManagerController {


    /*@PostMapping("/manager/show_users")
    public ResponseEntity<?> showUsers(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (checkToken(response, request)) {

            } else {
                return null;
            }
        } catch (Exception e) {
            sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
            return null;
        }


    }*/



}
