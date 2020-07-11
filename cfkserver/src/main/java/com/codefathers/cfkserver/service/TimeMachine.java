package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.exceptions.model.product.NoSuchSellerException;
import com.codefathers.cfkserver.model.entities.contents.Advertise;
import com.codefathers.cfkserver.model.entities.offs.DiscountCode;
import com.codefathers.cfkserver.model.entities.offs.Off;
import com.codefathers.cfkserver.model.entities.offs.OffStatus;
import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.entities.product.SellPackage;
import com.codefathers.cfkserver.model.repositories.AdvertiseRepository;
import com.codefathers.cfkserver.model.repositories.DiscountRepository;
import com.codefathers.cfkserver.model.repositories.OffRepository;
import com.codefathers.cfkserver.model.repositories.SellPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * TimeMachine is a {@link Component} that checks Start Date And End Date
 * of {@link DiscountCode}s, {@link Off}s and {@link Advertise}s and handles Activation Of Them.
 * Every 60_000 milliseconds it begins its operation
 */

@Component
public class TimeMachine {
    @Autowired
    private ContentService contentService;
    @Autowired
    private AdvertiseRepository advertiseRepository;
    @Autowired
    private DiscountRepository discountRepository;
    @Autowired
    private DiscountService discountService;
    @Autowired
    private OffRepository offRepository;
    @Autowired
    private SellPackageRepository sellPackageRepository;
    @Autowired
    private OffService offService;

    private final long TIME_PERIOD = 60_000;

    //@Scheduled(fixedRate = TIME_PERIOD)
    private void run() {
        offCheck();
        discountCheck();
        advertiseCheck();
    }

    private void advertiseCheck() {
        Date yesterday = new Date(new Date().getTime() - 86400000);
        List<Advertise> advertises = advertiseRepository.findAllByCreatedBeforeAndActiveIsTrue(yesterday);
        advertises.forEach(contentService::removeAdvertise);
    }

    private void discountCheck() {
        deleteExpiredCodes();
    }

    private void deleteExpiredCodes() {
        List<DiscountCode> results = discountRepository.findAllByEndTimeBefore(new Date());
        results.forEach(discountService::removeDiscount);
    }

    private void offCheck() {
        deleteExpiredOffs();
        activateNewOffs();
    }

    private void activateNewOffs() {
        List<Off> results = offRepository.findAllByStartTimeAfterAndOffStatusEquals(new Date(), OffStatus.ACCEPTED);
        results.forEach(this::addOffToProducts);
    }

    private void addOffToProducts(Off off) {
        off.setOffStatus(OffStatus.ACTIVATED);
        offRepository.save(off);
        String seller = off.getSeller().getUsername();
        for (Product product : off.getProducts()) {
            try {
                SellPackage sellPackage = product.findPackageBySeller(seller);
                sellPackage.setOff(off);
                sellPackage.setOnOff(true);
                sellPackageRepository.save(sellPackage);
            } catch (NoSuchSellerException ignore) {
            }
        }
    }

    private void deleteExpiredOffs() {
        List<Off> results = offRepository.findAllByEndTimeAfter(new Date());
        results.forEach(offService::deleteOff);
    }
}
