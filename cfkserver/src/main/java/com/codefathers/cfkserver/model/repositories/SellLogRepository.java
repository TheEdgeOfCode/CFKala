package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.logs.SellLog;
import org.springframework.data.repository.CrudRepository;

public interface SellLogRepository extends CrudRepository<SellLog, Integer> {
}
