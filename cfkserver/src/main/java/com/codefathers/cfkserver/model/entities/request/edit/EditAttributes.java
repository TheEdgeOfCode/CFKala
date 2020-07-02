package com.codefathers.cfkserver.model.entities.request.edit;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class EditAttributes {
    @Id @GeneratedValue
    @Column(updatable = false)
    protected int id;
    protected int sourceId;
}