package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.model.entities.user.Support;
import com.codefathers.cfkserver.model.repositories.SupportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SupportService {
    @Autowired
    private SupportRepository supportRepository;

}
