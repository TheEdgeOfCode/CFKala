package com.codefathers.anonymous_bank.controller;

import com.codefathers.anonymous_bank.model.dtos.ReceiptDTO;
import com.codefathers.anonymous_bank.model.entity.ReceiptType;
import com.codefathers.anonymous_bank.model.exceptions.receipt.InvalidDescriptionExcxeption;
import com.codefathers.anonymous_bank.model.exceptions.receipt.InvalidParameterPassedException;
import com.codefathers.anonymous_bank.model.exceptions.receipt.InvalidRecieptTypeException;
import com.codefathers.anonymous_bank.model.service.ReceiptService;
import com.codefathers.anonymous_bank.model.service.SecurityConfig;
import com.codefathers.anonymous_bank.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import static com.codefathers.anonymous_bank.utils.ErrorUtil.sendError;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class ReceiptController {
    @Autowired
    private ReceiptService receiptService;
    @Autowired private SecurityConfig security;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/create_receipt")
    private ResponseEntity<?> createReceipt(@RequestBody String body, HttpServletRequest request, HttpServletResponse response){
        try {
            if (security.validate(response,request)){
                return ResponseEntity.ok(receiptService.createReceipt(createReceiptDto(body)));
            }else {
                return null;
            }
        } catch (Exception e) {
            sendError(response,e.getMessage(), HttpStatus.BAD_REQUEST);
            return null;
        }
    }

    private ReceiptDTO createReceiptDto(String body)
            throws InvalidParameterPassedException, InvalidRecieptTypeException, InvalidDescriptionExcxeption
    {
        Matcher matcher = Pattern.compile("create_receipt (\\S+) (\\d{1,19}) (-?\\d{1,9}) (-?\\d{1,9}) (.*?)").matcher(body);
        if (matcher.find()){
            if (!matcher.group(1).matches("deposit|move|withdraw")){
                throw new InvalidRecieptTypeException();
            }else if (matcher.group(5).matches("(.*)[*?!:](.*)")){
                throw new InvalidDescriptionExcxeption("your input contains invalid characters");
            }else {
                return new ReceiptDTO(ReceiptType.from(matcher.group(1)),Long.parseLong(matcher.group(2)),
                        Integer.parseInt(matcher.group(3)),Integer.parseInt(matcher.group(4)),matcher.group(5));
            }
        }else {
            throw new InvalidParameterPassedException("invalid parameter passed");
        }
    }

    @PostMapping("get_transactions")
    private ResponseEntity<?> getTransactions(@RequestBody String body, HttpServletRequest request, HttpServletResponse response){
        String[] username = new String[1];
        if (security.validate(response,request,username)){
            try {
                return ResponseEntity.ok(receiptService.getTransactions(body,username[0]));
            } catch (Exception e) {
                sendError(response,e.getMessage(),HttpStatus.BAD_REQUEST);
                return null;
            }
        }else {
            return null;
        }
    }
}
