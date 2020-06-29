package com.codefathers.anonymous_bank.model.service;

import com.codefathers.anonymous_bank.model.repositories.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReceiptService {
    @Autowired
    private ReceiptRepository receiptRepository;
    // TODO: 6/28/2020
}
