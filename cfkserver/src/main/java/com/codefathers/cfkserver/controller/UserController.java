package com.codefathers.cfkserver.controller;

import com.codefathers.cfkserver.exceptions.model.company.NoSuchACompanyException;
import com.codefathers.cfkserver.exceptions.model.user.NotVerifiedSeller;
import com.codefathers.cfkserver.exceptions.model.user.UserAlreadyExistsException;
import com.codefathers.cfkserver.exceptions.model.user.UserNotFoundException;
import com.codefathers.cfkserver.exceptions.model.user.WrongPasswordException;
import com.codefathers.cfkserver.model.dtos.product.CreateProductDTO;
import com.codefathers.cfkserver.model.dtos.user.*;
import com.codefathers.cfkserver.service.UserService;
import com.codefathers.cfkserver.utils.ErrorUtil;
import com.codefathers.cfkserver.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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

    @PostMapping("/create_account")
    private <T> ResponseEntity<?> createAccount(@RequestBody CreateAccountDTO<T> userDTO, HttpServletResponse response){
        try {
            if (userDTO.getRole().equals("customer")) {
                CustomerDTO customerDTO = (CustomerDTO) userDTO.getInfo();
                return createCustomer(customerDTO);
            } else if (userDTO.getRole().equals("seller")) {
                userService.createSeller((SellerDTO) userDTO.getInfo());
                return ResponseEntity.ok(ResponseEntity.status(200));
            } else {
                ManagerDTO managerDTO = (ManagerDTO) userDTO.getInfo();
                return createManager(managerDTO);
            }
        } catch (UserAlreadyExistsException | NoSuchACompanyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    private ResponseEntity<?> createManager(ManagerDTO managerDTO) throws UserAlreadyExistsException {
        userService.createManager(managerDTO);
        String token = jwtUtil.generateToken(managerDTO.getUsername());
        return ResponseEntity.ok(new TokenRoleDto(token, "manager"));
    }

    private <T> ResponseEntity<?> createCustomer(CustomerDTO customerDTO) throws UserAlreadyExistsException {
        userService.createCustomer(customerDTO);
        String token = jwtUtil.generateToken(customerDTO.getUsername());
        return ResponseEntity.ok(new TokenRoleDto(token, "customer"));
    }
}
