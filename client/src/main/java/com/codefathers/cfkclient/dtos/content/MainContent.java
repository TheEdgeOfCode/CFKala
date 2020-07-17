package com.codefathers.cfkclient.dtos.content;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
public class MainContent {
    int id;
    String title;
    String Content;

    public MainContent(String title, String content) {
        this.title = title;
        Content = content;
    }

    @Override
    public String toString() {
        return title;
    }
}