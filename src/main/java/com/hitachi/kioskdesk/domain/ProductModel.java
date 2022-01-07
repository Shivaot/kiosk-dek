package com.hitachi.kioskdesk.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;

/**
 * Shiva Created on 07/01/22
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @Column(unique = true)
    String modelName;
    Date dateCreated;

    @PrePersist
    protected void onCreate() {
        dateCreated = new java.sql.Date(System.currentTimeMillis());
    }
}
