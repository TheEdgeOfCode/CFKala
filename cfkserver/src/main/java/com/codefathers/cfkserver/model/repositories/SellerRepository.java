package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.user.Seller;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerRepository extends CrudRepository<Seller,String> {
}
