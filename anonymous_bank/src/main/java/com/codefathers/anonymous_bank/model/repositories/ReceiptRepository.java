package com.codefathers.anonymous_bank.model.repositories;

import com.codefathers.anonymous_bank.model.entity.Receipt;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptRepository extends CrudRepository<Receipt,Integer> {
    List<Receipt> findAllBySourceAccountIs(int source);
    List<Receipt> findAllByDestAccountIs(int dest);
    List<Receipt> findAllByDestAccountIsOrSourceAccountIs(int dest,int source);
}
