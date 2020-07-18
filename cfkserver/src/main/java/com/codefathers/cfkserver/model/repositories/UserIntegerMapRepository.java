package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.maps.UserIntegerMap;
import com.codefathers.cfkserver.model.entities.user.User;
import org.springframework.data.repository.CrudRepository;

public interface UserIntegerMapRepository extends CrudRepository<UserIntegerMap, Integer> {
    UserIntegerMap findByUser(User user);
}
