package com.codefathers.cfkserver.controller;

import com.codefathers.cfkserver.model.dtos.user.UserFullDTO;
import com.codefathers.cfkserver.model.dtos.user.UserFullListDTO;
import com.codefathers.cfkserver.model.entities.user.User;
import com.codefathers.cfkserver.service.ManagerService;
import com.codefathers.cfkserver.service.Sorter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

import static com.codefathers.cfkserver.utils.ErrorUtil.sendError;
import static com.codefathers.cfkserver.utils.TokenUtil.checkToken;

@RestController
public class ManagerController {

    @Autowired
    private ManagerService managerService;


    @PostMapping("/manager/show_users")
    public ResponseEntity<?> showUsers(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (checkToken(response, request)) {
                List<User> allUsers = managerService.getAllUsers();
                List<UserFullDTO> userDTOS = new ArrayList<>();
                allUsers.forEach(user -> userDTOS.add(createUserFullDTO(user)));
                return ResponseEntity.ok(new UserFullListDTO(new ArrayList<>(userDTOS)));
            } else {
                return null;
            }
        } catch (Exception e) {
            sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
            return null;
        }
    }

    private UserFullDTO createUserFullDTO(User user) {
        return new UserFullDTO(user.getUsername(),
                user.getFirstName(),
                user.getLastName(), user.getEmail(), user.getPhoneNumber(),
                user.getClass().getName().split("\\.")[2]);
    }


}
