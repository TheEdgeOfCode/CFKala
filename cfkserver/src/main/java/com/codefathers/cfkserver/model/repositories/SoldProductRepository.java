package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.product.SoldProduct;
import org.springframework.data.repository.CrudRepository;

public interface SoldProductRepository extends CrudRepository<SoldProduct, Integer> {
}
