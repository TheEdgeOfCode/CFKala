package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.offs.Off;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OffRepository extends CrudRepository<Off,Integer> {
}
