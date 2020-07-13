package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.maps.UserIntegerMap;
import org.springframework.data.repository.CrudRepository;

public interface UserIntegerMapRepository extends CrudRepository<UserIntegerMap, Integer> {
}
