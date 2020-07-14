package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.logs.Log;
import org.springframework.data.repository.CrudRepository;

public interface LogRepository extends CrudRepository<Log, Integer> {
}
