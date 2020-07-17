package com.codefathers.cfkclient.dtos.product;

import lombok.Data;

@Data
public class CommentPM {
    private String userName;
    private String title;
    private String comment;
    private boolean boughThis;

    public CommentPM(String userName, String title, String comment, boolean boughThis) {
        this.userName = userName;
        this.title = title;
        this.comment = comment;
        this.boughThis = boughThis;
    }
}