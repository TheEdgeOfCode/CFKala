package com.codefathers.cfkserver.controller;

import com.codefathers.cfkserver.exceptions.model.content.SellerHasActiveAdException;
import com.codefathers.cfkserver.exceptions.model.product.NoSuchAProductException;
import com.codefathers.cfkserver.exceptions.token.ExpiredTokenException;
import com.codefathers.cfkserver.exceptions.token.InvalidTokenException;
import com.codefathers.cfkserver.model.dtos.category.CreateDTO;
import com.codefathers.cfkserver.model.dtos.content.AdPM;
import com.codefathers.cfkserver.model.dtos.content.CreateAd;
import com.codefathers.cfkserver.model.entities.contents.Advertise;
import com.codefathers.cfkserver.model.entities.contents.MainContent;
import com.codefathers.cfkserver.model.repositories.MainContentRepository;
import com.codefathers.cfkserver.service.ContentService;
import com.codefathers.cfkserver.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.codefathers.cfkserver.utils.ErrorUtil.sendError;
import static com.codefathers.cfkserver.utils.TokenUtil.checkToken;

@RestController
public class ContentController {
    @Autowired
    private ContentService contentService;
    @Autowired
    private ProductService productService;
    @Autowired
    private MainContentRepository mainContentRepository;

    @GetMapping("/content/all_ads")
    private List<AdPM> getAds() {
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
    private ResponseEntity<?> getMainContents() {
        Iterable<MainContent> all = mainContentRepository.findAll();
        return ResponseEntity.ok(StreamSupport.stream(all.spliterator(), false).collect(Collectors.toList()));
    }

    @PostMapping("/content/add_ad")
    private void addAd(@RequestBody CreateAd dto,
                       HttpServletRequest request, HttpServletResponse response) {
        try {
            if (checkToken(response, request)) {
                try {
                    contentService.Advertise(dto.getId(), dto.getUsername());
                } catch (SellerHasActiveAdException | NoSuchAProductException e) {
                    sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
                }
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/content/add_content")
    private void addContent(@RequestBody String body,HttpServletRequest request, HttpServletResponse response){

    }


    @PostMapping("/content/delete")
    private void deleteContent(@RequestBody Integer id,HttpServletRequest request, HttpServletResponse response) {
        try {
            if (checkToken(response, request)) {
                try {
                    contentService.deleteContent(id);
                } catch (Exception e) {
                    sendError(response, HttpStatus.BAD_REQUEST, e.getMessage());
                }
            }
        } catch (ExpiredTokenException | InvalidTokenException e) {
            sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
