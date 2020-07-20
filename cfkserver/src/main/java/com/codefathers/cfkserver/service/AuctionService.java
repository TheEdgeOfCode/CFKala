package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.exceptions.model.auction.AuctionNotFoundException;
import com.codefathers.cfkserver.exceptions.model.off.InvalidTimesException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchAProductException;
import com.codefathers.cfkserver.model.dtos.auction.CreateAuctionDTO;
import com.codefathers.cfkserver.model.entities.offs.Auction;
import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.entities.product.SellPackage;
import com.codefathers.cfkserver.model.entities.user.Seller;
import com.codefathers.cfkserver.model.repositories.AuctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuctionService {
    @Autowired private AuctionRepository auctionRepository;
    @Autowired private ProductService productService;

    public Auction getAuctionById(int id) throws AuctionNotFoundException {
        Optional<Auction> auction = auctionRepository.findById(id);
        if (auction.isPresent()){
            return auction.get();
        } else {
            throw new AuctionNotFoundException("Auction Was Not Found");
        }
    }

    public void createAuction(Seller seller, CreateAuctionDTO dto) throws InvalidTimesException, NoSuchAProductException {
        if (!dto.getStart().before(dto.getEnd())) throw new InvalidTimesException();
        Auction auction = new Auction();
        auction.setStartTime(dto.getStart());
        auction.setEndTime(dto.getEnd());

        Product product = productService.findById(dto.getProductId());
        //if (!product.isAvailable()) throw new PrExceptio
        SellPackage sellPackage = new SellPackage();
        sellPackage.set

        auctionRepository.save(auction);
    }
}
