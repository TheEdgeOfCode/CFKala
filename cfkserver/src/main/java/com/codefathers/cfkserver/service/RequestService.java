package com.codefathers.cfkserver.service;

import com.codefathers.cfkserver.model.entities.request.Request;
import com.codefathers.cfkserver.model.entities.request.RequestType;
import com.codefathers.cfkserver.model.repositories.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestService {
    @Autowired
    private RequestRepository requestRepository;

    public void createRequest(Object requestObject, RequestType type,String userRequested,String requset){
        Request request = new Request(userRequested,type,requset,requestObject);
        requestRepository.save(request);
    }
}
