package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.model.entities.user.User;
import com.codefathers.cfkserver.model.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public User getUserByUsername(String username){
        return accountRepository.getByUsername(username);
    }
}
