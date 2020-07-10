package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.offs.DiscountCode;
import org.springframework.data.repository.CrudRepository;

public interface DiscountRepository extends CrudRepository<DiscountCode, String> {
}
