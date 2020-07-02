package com.codefathers.cfkserver.model.entities.product;

import lombok.AccessLevel;
import lombok.*;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
public class Score {
    @Setter(AccessLevel.NONE)
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String userId;
    private int productId;
    private int score;

    public Score(String userId, int productId, int score) {
        this.userId = userId;
        this.productId = productId;
        this.score = score;
    }
}
