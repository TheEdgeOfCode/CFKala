package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.product.SellPackage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellPackageRepository extends CrudRepository<SellPackage,Integer> {
    List<SellPackage> findAllByOnOffTrue();
}
