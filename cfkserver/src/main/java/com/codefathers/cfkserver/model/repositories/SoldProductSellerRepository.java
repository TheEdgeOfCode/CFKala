package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.maps.SoldProductSellerMap;
import org.springframework.data.repository.CrudRepository;

public interface SoldProductSellerRepository extends CrudRepository<SoldProductSellerMap, Integer> {
}
