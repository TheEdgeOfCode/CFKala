package com.codefathers.cfkserver.model.entities.product;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@Data
public class Document {
    @Id @GeneratedValue
    private int id;
    private String name;
    private String format;;
    private String uri;
    private long size;
    @OneToOne
    private Product product;

    public Document(String name, String format, String uri, long size) {
        this.name = name;
        this.format = format;
        this.uri = uri;
        this.size = size;
    }
}
