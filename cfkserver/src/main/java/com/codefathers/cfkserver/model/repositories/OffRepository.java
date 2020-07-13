package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.offs.Off;
import com.codefathers.cfkserver.model.entities.offs.OffStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OffRepository extends CrudRepository<Off,Integer> {
    List<Off> findAllByStartTimeAfterAndOffStatusEquals(Date date, OffStatus offStatus);

    List<Off> findAllByEndTimeAfter(Date date);

}
