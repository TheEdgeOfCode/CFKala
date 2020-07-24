package com.codefathers.cfkserver.controller;

import com.codefathers.cfkserver.exceptions.model.bank.account.InvalidUsernameException;
import com.codefathers.cfkserver.exceptions.model.bank.account.PasswordsDoNotMatchException;
import com.codefathers.cfkserver.exceptions.model.bank.receipt.*;
import com.codefathers.cfkserver.exceptions.model.user.UserNotFoundException;
import com.codefathers.cfkserver.exceptions.token.ExpiredTokenException;
import com.codefathers.cfkserver.exceptions.token.InvalidTokenException;
import com.codefathers.cfkserver.model.dtos.bank.*;
import com.codefathers.cfkserver.model.dtos.edit.TollMinimumBalanceEditAttribute;
import com.codefathers.cfkserver.service.BankService;
import com.codefathers.cfkserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.codefathers.cfkserver.utils.ErrorUtil.sendError;
import static com.codefathers.cfkserver.utils.TokenUtil.checkToken;

@RestController
public class BankController {

    @Autowired
    private BankService bankService;
    @Autowired
    private UserService userService;

    @PostMapping("/bank/create_account")
    private ResponseEntity<?> createBankAccount(HttpServletRequest request, HttpServletResponse response,
                                                @RequestBody CreateBankAccountDTO dto) {
        try {
            if (checkToken(response, request)) {
                try {
                    return ResponseEntity.ok(bankService.createAccount(dto));
                } catch (IOException | InvalidUsernameException | PasswordsDoNotMatchException e) {
                    sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
                }
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
        return null;
    }

    @GetMapping("/bank/get_token/{username}")
    private ResponseEntity<?> getToken(HttpServletResponse response, @PathVariable String username) {
        try {
            String password = userService.getPassByUsername(username);
            return ResponseEntity.ok(bankService.getToken(new TokenRequestDTO(username, password)));
        } catch (Exception e) {
            sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return null;
    }

    @PostMapping("/bank/create_receipt")
    private ResponseEntity<?> createReceipt(HttpServletRequest request, HttpServletResponse response,
                                            @RequestBody CreateReceiptDTO dto) {
        try {
            if (checkToken(response, request)) {
                try {
                    return ResponseEntity.ok(bankService.createReceipt(dto));
                } catch (IOException | InvalidDescriptionExcxeption |
                        InvalidAccountIdException | EqualSourceDestException |
                        InvalidDestAccountException | InvalidSourceAccountException |
                        InvalidParameterPassedException | InvalidMoneyException |
                        InvalidRecieptTypeException | InvalidUsernameException e) {
                    sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
                }
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
        return null;
    }

    @GetMapping("bank/get_transactions")
    private ResponseEntity<?> getTransactions(HttpServletRequest request, HttpServletResponse response,
                                              @RequestBody NeededForTransactionDTO dto) {
        try {
            if (checkToken(response, request)) {
                try {
                    List<TransactionDTO> transactionDTOS = bankService.getTransactions(dto);
                    return ResponseEntity.ok(new TransactionListDTO(new ArrayList<>(transactionDTOS)));
                } catch (IOException | InvalidReceiptIdException | InvalidUsernameException e) {
                    sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
                }
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
        return null;
    }

    @PostMapping("/bank/pay")
    private void pay(HttpServletRequest request, HttpServletResponse response,
                                  @RequestBody String info) {
        try {
            if (checkToken(response, request)) {
                try {
                    bankService.pay(Integer.parseInt(info));
                } catch (IOException | InvalidAccountIdException |
                        NotEnoughMoneyAtSourceException | PaidReceiptException |
                        InvalidReceiptIdException e) {
                    sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
                }
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping
    @RequestMapping("/bank/get_balance/{username}")
    private Long getBalance(HttpServletRequest request, HttpServletResponse response,
                                         @PathVariable String username) {
        try {
            if (checkToken(response, request)) {
                try {
                    String password = userService.getPassByUsername(username);
                    String token = bankService.getToken(new TokenRequestDTO(username, password));
                    return bankService.getBalance(new BalanceDTO(username, password, token));
                } catch (IOException | InvalidUsernameException e) {
                    sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
                } catch (UserNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
        return null;
    }

    @GetMapping("/bank/get_info")
    private ResponseEntity<?> getManagerInfo(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (checkToken(response, request)) {
                String accountId = bankService.getInfo("AccountId");
                String toll = bankService.getInfo("Toll");
                String minimum_balance = bankService.getInfo("Minimum Balance");
                InfoDTO infoDTO = new InfoDTO(accountId, toll, minimum_balance);
                return ResponseEntity.ok(infoDTO);
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
        return null;
    }

    @PostMapping("/bank/edit_info")
    private ResponseEntity<?> editTollMinimumBalanceInfo(HttpServletRequest request, HttpServletResponse response,
                                                         @RequestBody TollMinimumBalanceEditAttribute attribute) {
        try {
            if (checkToken(response, request)) {
                System.out.println("aaa");
                bankService.editTollMinimumBalanceInfo(attribute);
                return ResponseEntity.ok(ResponseEntity.status(200));
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
        return null;
    }

}
