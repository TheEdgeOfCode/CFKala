package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.logs.PurchaseLog;
import org.springframework.data.repository.CrudRepository;

public interface PurchaseLogRepository extends CrudRepository<PurchaseLog, Integer> {
}
