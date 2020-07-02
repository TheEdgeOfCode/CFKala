package com.codefathers.cfkserver.model.entities.user;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "t_massage")
public class Message {
    @Setter(AccessLevel.NONE)
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String subject;
    private String message;

    @Temporal(TemporalType.DATE)
    private Date date;
    boolean isRead;
}