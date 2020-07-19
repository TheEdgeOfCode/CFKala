package com.codefathers.cfkclient.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

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
