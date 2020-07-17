package com.codefathers.cfkserver.controller;

import com.codefathers.cfkserver.exceptions.token.ExpiredTokenException;
import com.codefathers.cfkserver.exceptions.token.InvalidTokenException;
import com.codefathers.cfkserver.model.dtos.discount.AddUser;
import com.codefathers.cfkserver.model.dtos.discount.CreateDiscount;
import com.codefathers.cfkserver.model.dtos.discount.CreateDiscountSystematic;
import com.codefathers.cfkserver.model.entities.request.edit.DiscountCodeEditAttributes;
import com.codefathers.cfkserver.model.entities.user.Customer;
import com.codefathers.cfkserver.model.entities.user.User;
import com.codefathers.cfkserver.service.CustomerService;
import com.codefathers.cfkserver.service.DiscountService;
import com.codefathers.cfkserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.codefathers.cfkserver.utils.ErrorUtil.sendError;
import static com.codefathers.cfkserver.utils.TokenUtil.checkToken;

@RestController
public class DiscountController {
    @Autowired
    private DiscountService discountService;
    @Autowired
    private UserService userService;
    @Autowired
    private CustomerService customerService;


    @PostMapping("/discount/systematic")
    private void systematicDiscount(@RequestBody CreateDiscountSystematic dto,
                                    HttpServletRequest request, HttpServletResponse response) {
        try {
            if (checkToken(response, request)) {
                try {
                    String code = dto.getCode();
                    int amount = dto.getAmount();
                    SysDis sysDis = dto.getSysDis();
                    switch (sysDis) {
                        case TO10:
                            discountService.sysTo(code, amount, 10);
                        case TO50:
                            discountService.sysTo(code, amount, 50);
                        case TO100:
                            discountService.sysTo(code, amount, 100);
                        case MORE1500:
                            discountService.sysMore(code, amount, 1500);
                        case MORE2500:
                            discountService.sysMore(code, amount, 2000);
                    }
                } catch (Exception e) {
                    sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
                }
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/discount/delete")
    private void systematicDiscount(@RequestBody String code,
                                    HttpServletRequest request, HttpServletResponse response) {
        try {
            if (checkToken(response, request)) {
                try {
                    discountService.removeDiscount(code);
                } catch (Exception e) {
                    sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
                }
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/discount/edit")
    private void editDiscount(@RequestBody DiscountCodeEditAttributes attributes,
                              HttpServletRequest request, HttpServletResponse response) {
        try {
            if (checkToken(response, request)) {
                try {
                    discountService.editDiscountCode(attributes.getCode(), attributes);
                } catch (Exception e) {
                    sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
                }
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/discount/create")
    private void createDiscount(@RequestBody CreateDiscount dto,
                                HttpServletRequest request, HttpServletResponse response) {
        try {
            if (checkToken(response, request)) {
                try {
                    String[] data = dto.getData();
                    int offPercentage = Integer.parseInt(data[1]);
                    long maxDiscount = Long.parseLong(data[2]);
                    discountService.createDiscountCode(data[0], dto.getStart(), dto.getEnd(),
                            offPercentage, maxDiscount);
                } catch (Exception e) {
                    sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
                }
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/discount/remove_user")
    private void createDiscount(@RequestBody String body,
                                HttpServletRequest request, HttpServletResponse response) {
        try {
            if (checkToken(response, request)) {
                try {
                    String[] split = body.split("~~~");
                    User user = userService.getUserByUsername(split[1]);
                    discountService.removeUserFromDiscountCodeUsers(split[0], user);
                } catch (Exception e) {
                    sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
                }
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/discount/add_user")
    private void createDiscount(@RequestBody AddUser dto,
                                HttpServletRequest request, HttpServletResponse response) {
        try {
            if (checkToken(response, request)) {
                try {
                    Customer customer = customerService.getCustomerByUsername(dto.getUsername());
                    discountService.addUserToDiscountCodeUsers(dto.getCode(), customer, dto.getAmount());
                } catch (Exception e) {
                    sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
                }
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/discount/get_discounts")
    private ResponseEntity<?> createDiscount(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (checkToken(response, request)) {
                try {
                    return ResponseEntity.ok(discountService.getDiscountCodes());
                } catch (Exception e) {
                    sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
                    return null;
                }
            } else {
                return null;
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
            return null;
        }
    }

    public enum SysDis {
        TO10,
        TO50,
        TO100,
        MORE1500,
        MORE2500
    }
}
