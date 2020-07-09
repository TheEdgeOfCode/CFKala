package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.request.edit.ProductEditAttribute;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductEditAttributeRepository extends CrudRepository<ProductEditAttribute,Integer> {
}
