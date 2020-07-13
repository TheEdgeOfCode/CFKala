package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.product.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends CrudRepository<Category,Integer> {
    List<Category> findAllByParentIsNull();
}
