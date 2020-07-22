package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.product.Document;
import org.springframework.data.repository.CrudRepository;

public interface DocumentRepository extends CrudRepository<Document,Integer> {
}
