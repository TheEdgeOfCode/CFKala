package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.product.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category,Integer> {
}
