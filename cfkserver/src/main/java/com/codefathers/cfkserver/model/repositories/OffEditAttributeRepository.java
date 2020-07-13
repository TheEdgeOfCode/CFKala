package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.request.edit.OffChangeAttributes;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OffEditAttributeRepository extends CrudRepository<OffChangeAttributes,Integer> {
}
