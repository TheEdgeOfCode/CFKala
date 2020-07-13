package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.contents.MainContent;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MainContentRepository extends CrudRepository<MainContent, Integer> {
}
