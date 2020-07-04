package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.product.Company;
import org.springframework.data.repository.CrudRepository;

public interface CompanyRepository extends CrudRepository<Company,Integer> {
}
