package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.offs.Off;
import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.entities.request.Request;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends CrudRepository<Request,Integer> {
    List<Request> findAllByProduct(Product product);
    List<Request> findAllByOff(Off off);
    List<Request> findAllByDoneIsFalse();
}
