package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.product.Comment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends CrudRepository<Comment,Integer> {
}
