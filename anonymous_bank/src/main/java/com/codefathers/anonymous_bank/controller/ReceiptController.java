package com.codefathers.anonymous_bank.controller;

import com.codefathers.anonymous_bank.model.service.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReceiptController {
    @Autowired
    private ReceiptService receiptService;

    @PostMapping("/create_receipt")
    private void createReceipt(@RequestBody String string){

    }
}
