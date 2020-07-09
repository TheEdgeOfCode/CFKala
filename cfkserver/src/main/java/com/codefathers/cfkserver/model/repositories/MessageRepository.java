package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.user.Message;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<Message,Integer> {
}
