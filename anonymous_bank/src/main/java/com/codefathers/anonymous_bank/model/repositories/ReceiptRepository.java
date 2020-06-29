package com.codefathers.anonymous_bank.model.repositories;

import com.codefathers.anonymous_bank.model.entity.Receipt;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptRepository extends CrudRepository<Receipt,Integer> {
    
}
