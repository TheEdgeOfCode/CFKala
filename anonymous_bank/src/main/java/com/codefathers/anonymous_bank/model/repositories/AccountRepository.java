package com.codefathers.anonymous_bank.model.repositories;

import com.codefathers.anonymous_bank.model.entity.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends CrudRepository<Account,Integer> {
    Optional<Account> findByUsername(String username);
    boolean existsAccountByUsername(String username);
    boolean existsAccountById(int id);
}
