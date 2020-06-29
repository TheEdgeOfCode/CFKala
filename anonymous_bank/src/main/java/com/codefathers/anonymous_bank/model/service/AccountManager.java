package com.codefathers.anonymous_bank.model.service;

import com.codefathers.anonymous_bank.model.dtos.AccountDTO;
import com.codefathers.anonymous_bank.model.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountManager {

    @Autowired
    private AccountRepository accountRepository;

    public void createAccount(AccountDTO dto){
        // TODO: 6/28/2020
    }
}
