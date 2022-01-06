package com.hitachi.kioskdesk.domain;

import com.hitachi.kioskdesk.enums.ModelEnum;
import com.hitachi.kioskdesk.enums.RejectionScrap;
import com.hitachi.kioskdesk.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;

/**
 * Shiva Created on 04/01/22
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String partNumber;
    private Integer quantity;
    @Enumerated(EnumType.STRING)
    private ModelEnum model;
    private Date date;
    @Column(columnDefinition = "TEXT")
    private String defectDescription;
    @Column(columnDefinition = "TEXT")
    private String qc1Comments;
    @Column(columnDefinition = "TEXT")
    private Date dateCreated;
    private Date lastUpdated;
    private String creatorName;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Enumerated(EnumType.STRING)
    private RejectionScrap rejectionOrScrap;
    private String vendor;
    private String notificationNumber;
    private String authorizedBy;

    @PrePersist
    protected void onCreate() {
        dateCreated = new Date(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = new Date(System.currentTimeMillis());
    }
}
