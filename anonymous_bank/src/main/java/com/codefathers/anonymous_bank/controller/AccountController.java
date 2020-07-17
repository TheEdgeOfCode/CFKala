package com.codefathers.anonymous_bank.controller;

import com.codefathers.anonymous_bank.model.dtos.AccountDTO;
import com.codefathers.anonymous_bank.model.exceptions.account.InvalidUsernameException;
import com.codefathers.anonymous_bank.model.service.AccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class AccountController {
    @Autowired
    private AccountManager accountManager;

    @PostMapping("/createAccount")
    private int createAccount(@RequestBody String request) throws Exception {
        AccountDTO accountDTO = createDtoFromRequest(request);
        if (accountDTO != null) {
            accountManager.checkForValidUsername(accountDTO.getUsername());
            return accountManager.createAccount(accountDTO);
        }
        return 0;
    }

    private AccountDTO createDtoFromRequest(String request) {
        Matcher matcher = Pattern.compile("create_account (\\S+) (\\S+) (\\S+) (\\S+) (\\S+)").matcher(request);
        if (matcher.find()){
            AccountDTO accountDTO = new AccountDTO();
            accountDTO.setFirstName(matcher.group(1));
            accountDTO.setLastName(matcher.group(2));
            accountDTO.setUsername(matcher.group(3));
            accountDTO.setPassword(matcher.group(4));
            accountDTO.setRePass(matcher.group(5));
            return accountDTO;
        }
        return null;

    }
}
