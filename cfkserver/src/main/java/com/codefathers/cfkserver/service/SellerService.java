package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.exceptions.model.product.NoSuchSellerException;
import com.codefathers.cfkserver.model.entities.user.Seller;
import com.codefathers.cfkserver.model.repositories.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SellerService {
    @Autowired
    private SellerRepository sellerRepository;

    public Seller findSellerByUsername(String username) throws NoSuchSellerException {
        Optional<Seller> seller = sellerRepository.findById(username);
        if (seller.isPresent()){
            return seller.get();
        }else {
            throw new NoSuchSellerException("Seller ("+ username + ") Doesn't Exist");
        }
    }

    public void saveSeller(Seller seller){
        sellerRepository.save(seller);
    }
}
