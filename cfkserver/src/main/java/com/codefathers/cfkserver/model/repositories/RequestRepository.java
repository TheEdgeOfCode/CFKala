package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.request.Request;
import org.springframework.data.repository.CrudRepository;

public interface RequestRepository extends CrudRepository<Request,Integer> {
}
