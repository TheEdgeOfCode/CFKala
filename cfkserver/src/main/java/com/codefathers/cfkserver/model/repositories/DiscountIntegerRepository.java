package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.maps.DiscountcodeIntegerMap;
import org.springframework.data.repository.CrudRepository;

public interface DiscountIntegerRepository extends CrudRepository<DiscountcodeIntegerMap, Integer> {
}
