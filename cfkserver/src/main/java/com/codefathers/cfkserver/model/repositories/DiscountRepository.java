package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.offs.DiscountCode;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface DiscountRepository extends CrudRepository<DiscountCode, String> {
    List<DiscountCode> findAllByEndTimeBefore(Date date);
}
