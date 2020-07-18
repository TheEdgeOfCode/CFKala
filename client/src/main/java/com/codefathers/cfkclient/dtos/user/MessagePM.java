package com.codefathers.cfkclient.dtos.user;

import lombok.Data;

import java.util.Date;

@Data
public class MessagePM {
    private int id;
    private String subject;
    private String message;
    private Boolean isRead;
    private Date date;

    public MessagePM(int id,String subject, String message, boolean isRead, Date date) {
        this.id = id;
        this.subject = subject;
        this.message = message;
        this.isRead = isRead;
        this.date = date;
    }
}
