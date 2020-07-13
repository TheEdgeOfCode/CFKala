package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.user.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends CrudRepository<Message,Integer> {
}
