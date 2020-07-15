package com.codefathers.cfkserver.controller;

import com.codefathers.cfkserver.exceptions.model.company.NoSuchACompanyException;
import com.codefathers.cfkserver.exceptions.model.user.NotVerifiedSeller;
import com.codefathers.cfkserver.exceptions.model.user.UserAlreadyExistsException;
import com.codefathers.cfkserver.exceptions.model.user.UserNotFoundException;
import com.codefathers.cfkserver.exceptions.model.user.WrongPasswordException;
import com.codefathers.cfkserver.model.dtos.user.*;
import com.codefathers.cfkserver.model.entities.product.Company;
import com.codefathers.cfkserver.model.entities.request.Request;
import com.codefathers.cfkserver.model.entities.request.edit.UserEditAttributes;
import com.codefathers.cfkserver.model.entities.user.Customer;
import com.codefathers.cfkserver.model.entities.user.Seller;
import com.codefathers.cfkserver.model.entities.user.User;
import com.codefathers.cfkserver.service.CompanyService;
import com.codefathers.cfkserver.service.CustomerService;
import com.codefathers.cfkserver.service.SellerService;
import com.codefathers.cfkserver.service.UserService;
import com.codefathers.cfkserver.utils.ErrorUtil;
import com.codefathers.cfkserver.utils.JwtUtil;
import com.codefathers.cfkserver.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.codefathers.cfkserver.utils.ErrorUtil.sendError;
import static org.springframework.http.HttpStatus.*;

@RestController
public class UserController {
    private HashMap<String, String> tokens;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private SellerService sellerService;
    @Autowired
    private CustomerService customerService;

    @PostMapping("users/login")
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

    @PostMapping("users/create_account")
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
            sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
            return null;
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

    @GetMapping("users/view")
    public ResponseEntity<?> viewPersonalInfo(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (TokenUtil.checkToken(response, request)) {
                User user = userService.viewPersonalInfo(TokenUtil.getUsernameFromToken(request));
                UserFullDTO dto = new UserFullDTO(
                        user.getUsername(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getPhoneNumber(),
                        user.getClass().getName().split("\\.")[2]
                );
                return ResponseEntity.ok(dto);
            }
            else
                return null;
        } catch (Exception e) {
            sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
            return null;
        }
    }

    @PutMapping("users/edit")
    public ResponseEntity<?> editPersonalInfo(HttpServletRequest request, HttpServletResponse response,
                                              @RequestBody UserEditAttributes editAttributes) {
        try {
            if (TokenUtil.checkToken(response, request)) {
                userService.changeInfo(TokenUtil.getUsernameFromToken(request), editAttributes);
                return ResponseEntity.ok(HttpStatus.valueOf(200));
            }
            else
                return null;
        } catch (Exception e) {
            ErrorUtil.sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
            return null;
        }
    }

    @PostMapping("users/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (TokenUtil.checkToken(response, request)) {
                String username = TokenUtil.getUsernameFromToken(request);
                userService.logout(username);
                tokens.remove(username);
                return ResponseEntity.ok(HttpStatus.valueOf(200));
            }
            else
                return null;
        } catch (Exception e) {
            sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
            return null;
        }
    }

    @PostMapping("users/create_company")
    public ResponseEntity<?> createCompany(String[] info) {
        Company company = new Company(info[0], info[1], info[2]);
        int companyId = companyService.createCompany(company);
        return ResponseEntity.ok(companyId);
    }

    @GetMapping("users/requests")
    public ResponseEntity<?> viewRequestSent(HttpServletRequest request, HttpServletResponse response, @RequestBody String role) {
        try {
            if (TokenUtil.checkToken(response, request)) {
                String username = TokenUtil.getUsernameFromToken(request);
                List<Request> requests = new ArrayList<>();
                if (role.equalsIgnoreCase("seller")) {
                    Seller seller = sellerService.findSellerByUsername(username);
                    if (seller != null) {
                        requests = seller.getRequests();
                    }
                } else if (role.equalsIgnoreCase("customer")) {
                    Customer customer = customerService.getCustomerByUsername(username);
                    if (customer != null) {
                        requests = customer.getRequests();
                    }
                }

                if (requests == null || requests.isEmpty()) {
                    return ResponseEntity.ok(HttpStatus.valueOf(200));
                } else {
                    return ResponseEntity.ok(new RequestsListDTO(makeRequestDTOs(requests)));
                }
            }
            else
                return null;
        } catch (Exception e) {
            sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
            return null;
        }
    }

    private ArrayList<RequestDTO> makeRequestDTOs(List<Request> requests) {
        ArrayList<RequestDTO> requestPMS = new ArrayList<>();
        requests.forEach(req -> requestPMS.add(newReqDTO(req)));
        return requestPMS;
    }

    private RequestDTO newReqDTO(Request req) {
        String status;
        if (!req.isDone()) {
            status = "Not Considered Yet";
        } else {
            if (req.isAccepted()) {
                status = "Accepted";
            } else {
                status = "Rejected";
            }
        }
        return new RequestDTO(req.getRequestId(), status, req.getRequest());
    }

}
