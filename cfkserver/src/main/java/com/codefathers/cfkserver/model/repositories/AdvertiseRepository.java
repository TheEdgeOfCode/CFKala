package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.contents.Advertise;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface AdvertiseRepository extends CrudRepository<Advertise,Integer> {
    List<Advertise> findAllByCreatedBeforeAndActiveIsTrue(Date created);

    List<Advertise> findAllByActiveIsTrue();
    void deleteAllByCreatedBeforeAndActiveIsTrue(Date date);
}
