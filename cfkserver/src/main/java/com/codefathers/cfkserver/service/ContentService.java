package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.exceptions.model.content.SellerHasActiveAdException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchAProductException;
import com.codefathers.cfkserver.model.entities.contents.Advertise;
import com.codefathers.cfkserver.model.entities.contents.MainContent;
import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.entities.request.Request;
import com.codefathers.cfkserver.model.entities.request.RequestType;
import com.codefathers.cfkserver.model.repositories.AdvertiseRepository;
import com.codefathers.cfkserver.model.repositories.MainContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentService {
    private final MainContentRepository mainContentRepository;
    private final AdvertiseRepository advertiseRepository;
    private final ProductService productService;
    private final RequestService requestService;

    @Autowired
    public ContentService(MainContentRepository mainContentRepository, AdvertiseRepository advertiseRepository, ProductService productService, RequestService requestService) {
        this.mainContentRepository = mainContentRepository;
        this.advertiseRepository = advertiseRepository;
        this.productService = productService;
        this.requestService = requestService;
    }

    public void addContent(String title, String content) {
        MainContent mainContent = new MainContent(title, content);
        mainContentRepository.save(mainContent);
    }

    public void deleteContent(int id) {
        mainContentRepository.deleteById(id);
    }

    public void Advertise(int id, String username) throws SellerHasActiveAdException, NoSuchAProductException {
        checkIfAdvertisedYet(username);
        Product product = productService.findById(id);
        Advertise advertise = new Advertise(product, username);
        advertiseRepository.save(advertise);
        Request request = requestService.createRequest(advertise, RequestType.ADVERTISE,
                username + "has requested to create an ad on product " + product.getId(),
                username);
    }

    private void checkIfAdvertisedYet(String username) throws SellerHasActiveAdException {
        List<Advertise> activeAdvertises = getActiveAdvertises();
        for (Advertise advertise : activeAdvertises) {
            if (advertise.getUsername().equals(username))
                throw new SellerHasActiveAdException("You Have Active Ad");
        }
    }

    public List<Advertise> getActiveAdvertises() {
        return advertiseRepository.findAllByActiveIsTrue();
    }

    void removeAdvertise(Advertise advertise) {
        advertise.setProduct(null);
        advertiseRepository.delete(advertise);
    }
}
