package com.codefathers.cfkserver.model.entities.product;

import lombok.AccessLevel;
import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
public class Comment {
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue
    private int id;
    private String userId;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String text;

    @Enumerated(EnumType.STRING)
    private CommentStatus status;

    @ManyToOne
    private Product product;
    private boolean boughtThisProduct;

    public Comment(String userId, String title, String text, CommentStatus status, boolean boughtThisProduct) {
        this.userId = userId;
        this.title = title;
        this.text = text;
        this.status = status;
        this.boughtThisProduct = boughtThisProduct;
    }
}