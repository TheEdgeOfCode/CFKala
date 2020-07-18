package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.entities.user.Seller;
import com.codefathers.cfkserver.model.entities.user.SubCart;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCartRepository extends CrudRepository<SubCart,Integer> {
    List<SubCart> findAllByProductAndSeller(Product product, Seller seller);
    List<SubCart> findAllBySeller(Seller seller);
}
