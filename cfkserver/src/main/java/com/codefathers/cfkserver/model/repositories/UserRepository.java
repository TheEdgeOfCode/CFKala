package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User,String> {
    List<User> findAllBy();
}
