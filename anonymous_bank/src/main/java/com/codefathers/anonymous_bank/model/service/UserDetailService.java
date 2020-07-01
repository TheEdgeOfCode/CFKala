package com.codefathers.anonymous_bank.model.service;

import com.codefathers.anonymous_bank.model.entity.Account;
import com.codefathers.anonymous_bank.model.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserDetailService implements UserDetailsService {
    @Autowired
    private AccountRepository repository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> account = repository.findByUsername(username);
        if (account.isPresent()){
            return new User(account.get().getUsername(),account.get().getPassword(),new ArrayList<>());
        }
        else throw new UsernameNotFoundException(username);
    }
}
