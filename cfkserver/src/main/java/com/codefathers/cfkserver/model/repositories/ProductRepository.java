package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.entities.product.ProductStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<Product,Integer> {
    List<Product> findAllByProductStatusEquals(ProductStatus status);
    List<Product> findAllByName(String name);
}
