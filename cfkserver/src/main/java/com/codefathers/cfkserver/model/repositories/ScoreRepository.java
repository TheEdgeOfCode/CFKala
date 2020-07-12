package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.product.Score;
import org.springframework.data.repository.CrudRepository;

public interface ScoreRepository extends CrudRepository<Score, Integer> {
}
