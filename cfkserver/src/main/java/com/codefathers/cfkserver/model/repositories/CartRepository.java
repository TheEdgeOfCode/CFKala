package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.user.Cart;
import org.springframework.data.repository.CrudRepository;

public interface CartRepository extends CrudRepository<Cart,Integer> {
}
