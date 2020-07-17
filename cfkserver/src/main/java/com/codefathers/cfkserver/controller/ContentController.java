package com.codefathers.cfkserver.controller;

import com.codefathers.cfkserver.exceptions.model.content.SellerHasActiveAdException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchAProductException;
import com.codefathers.cfkserver.model.dtos.content.AdPM;
import com.codefathers.cfkserver.model.entities.contents.Advertise;
import com.codefathers.cfkserver.model.entities.contents.MainContent;
import com.codefathers.cfkserver.model.repositories.MainContentRepository;
import com.codefathers.cfkserver.service.ContentService;
import com.codefathers.cfkserver.service.ProductService;
import org.jboss.jandex.Main;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
public class ContentController {
    @Autowired
    private ContentService contentService;
    @Autowired
    private ProductService productService;
    @Autowired
    private MainContentRepository mainContentRepository;

    @GetMapping("/content/all_ads")
    public List<AdPM> getAds() {
        List<Advertise> activeAdvertises = contentService.getActiveAdvertises();
        List<AdPM> list = new ArrayList<>();
        activeAdvertises.forEach(advertise -> list.add(createAdPm(advertise)));
        return list;
    }

    private AdPM createAdPm(Advertise advertise) {
        int productId = advertise.getProduct().getId();
        byte[] image = productService.getMainImageForProduct(productId);
        return new AdPM(productId, advertise.getProduct().getName(), advertise.getUsername(), image);
    }

    @GetMapping("/content/get_main_contents")
    public List<MainContent> getMainContents() {
        Iterable<MainContent> all = mainContentRepository.findAll();
        return StreamSupport.stream(all.spliterator(), false).collect(Collectors.toList());
    }

    /*
    public void addAd(int id, String username)
            throws SellerHasActiveAdException, NoSuchAProductException {
        contentManager.Advertise(id, username);
    }*/

    /*
    public void addContent(String title, String content){
        contentManager.addContent(title, content);
    }

    public void deleteContent(int id) {
        contentManager.deleteContent(id);
    }*/

}
