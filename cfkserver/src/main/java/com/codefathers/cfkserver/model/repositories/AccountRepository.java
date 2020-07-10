package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.user.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AccountRepository extends CrudRepository<User, String> {
    User getByUsername(String username);
}
