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

@Component
public class TimeMachine {
    private final ContentService contentService;
    private final AdvertiseRepository advertiseRepository;
    private final DiscountRepository discountRepository;
    private final DiscountService discountService;
    private final OffRepository offRepository;
    private final SellPackageRepository sellPackageRepository;
    private final OffService offService;

    private final long TIME_PERIOD = 60_000;

    @Autowired
    public TimeMachine(ContentService contentService, AdvertiseRepository advertiseRepository,
                       DiscountRepository discountRepository, DiscountService discountService,
                       OffRepository offRepository, SellPackageRepository sellPackageRepository, OffService offService) {
        this.contentService = contentService;
        this.advertiseRepository = advertiseRepository;
        this.discountRepository = discountRepository;
        this.discountService = discountService;
        this.offRepository = offRepository;
        this.sellPackageRepository = sellPackageRepository;
        this.offService = offService;
    }

    @Scheduled(fixedRate = TIME_PERIOD)
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
