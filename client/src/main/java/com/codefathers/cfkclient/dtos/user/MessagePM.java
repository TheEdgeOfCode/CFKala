package com.codefathers.cfkclient.dtos.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor

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
