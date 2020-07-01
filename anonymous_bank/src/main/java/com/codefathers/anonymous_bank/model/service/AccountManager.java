package com.codefathers.anonymous_bank.model.service;

import com.codefathers.anonymous_bank.model.dtos.AccountDTO;
import com.codefathers.anonymous_bank.model.entity.Account;
import com.codefathers.anonymous_bank.model.exceptions.account.InvalidUsernameException;
import com.codefathers.anonymous_bank.model.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountManager {

    @Autowired
    private AccountRepository accountRepository;

    public int createAccount(AccountDTO dto){
        Account account = new Account(dto.getUsername(),dto.getFirstName(),dto.getLastName(),dto.getPassword());
        accountRepository.save(account);
        return account.getId();
    }

    public void checkForValidUsername(String username) throws InvalidUsernameException {
        if (accountRepository.existsAccountByUsername(username))
            throw new InvalidUsernameException("Invalid Username");
    }
}
