package com.codefathers.cfkserver.model.repositories;

import com.codefathers.cfkserver.model.entities.offs.Auction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionRepository extends CrudRepository<Auction, Integer> {
    List<Auction> findAllBy();
}
