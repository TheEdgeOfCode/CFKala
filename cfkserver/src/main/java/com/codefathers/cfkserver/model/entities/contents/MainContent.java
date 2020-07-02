package com.codefathers.cfkserver.model.entities.contents;

import lombok.*;
import javax.persistence.*;

@Entity
@Data @NoArgsConstructor
public class MainContent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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