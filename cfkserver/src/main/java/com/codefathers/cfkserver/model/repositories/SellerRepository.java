package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.user.Seller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

public interface SellerRepository extends CrudRepository<Seller,String> {

}
