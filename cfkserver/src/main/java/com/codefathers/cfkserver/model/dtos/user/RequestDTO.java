package com.codefathers.cfkserver.model.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestDTO {
    private String requesterUserName;
    private int requestId;
    private String requestType;
    private String request;

    public RequestDTO(int requestId, String requestType, String request) {
        this.requestId = requestId;
        this.requestType = requestType;
        this.request = request;
    }
}
