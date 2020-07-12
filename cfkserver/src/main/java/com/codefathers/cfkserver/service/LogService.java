package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.model.repositories.PurchaseLogRepository;
import com.codefathers.cfkserver.model.repositories.SellLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogService {
    @Autowired
    private PurchaseLogRepository purchaseLogRepository;
    @Autowired
    private SellLogRepository sellLogRepository;
}
