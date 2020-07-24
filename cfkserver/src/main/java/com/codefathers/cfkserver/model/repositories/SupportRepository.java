package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.user.Support;
import org.springframework.data.repository.CrudRepository;

public interface SupportRepository extends CrudRepository<Support,String> {
}
