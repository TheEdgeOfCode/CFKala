package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.contents.Advertise;
import org.springframework.data.repository.CrudRepository;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;

public interface AdvertiseRepository extends CrudRepository<Advertise,Integer> {
    List<Advertise> findAllByCreatedAndActiveIsTrue(Date created);
    void deleteAllByCreatedBeforeAndActiveIsTrue(Date date);
}
