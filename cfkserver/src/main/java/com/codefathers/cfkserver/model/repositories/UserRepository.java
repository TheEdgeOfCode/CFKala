package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.user.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User,String> {
}
