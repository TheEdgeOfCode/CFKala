package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.entities.request.Request;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RequestRepository extends CrudRepository<Request,Integer> {
    List<Request> findAllByProduct(Product product);
}
