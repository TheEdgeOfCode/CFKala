package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.user.Manager;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerRepository extends CrudRepository<Manager, String> {

}
