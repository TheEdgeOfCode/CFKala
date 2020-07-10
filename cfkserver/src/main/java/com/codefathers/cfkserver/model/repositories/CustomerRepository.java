package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.user.Customer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomerRepository extends CrudRepository<Customer, String> {
    List<Customer> findAllByAllPurchaseIsGreaterThan(long amount);
}
