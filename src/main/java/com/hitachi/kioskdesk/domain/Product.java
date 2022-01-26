package com.hitachi.kioskdesk.domain;

import com.hitachi.kioskdesk.enums.ModelEnum;
import com.hitachi.kioskdesk.enums.RejectionScrap;
import com.hitachi.kioskdesk.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
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
    @Size(min = 2, max = 20, message = "min 2 and max 20 characters are allowed for part number")
    private String partNumber;
    private Integer quantity;
    private String model;
    private Date date;
    @Column(columnDefinition = "TEXT")
    @Size(min = 2, max = 100, message = "min 2 and max 100 characters are allowed")
    private String defectDescription;
    @Column(columnDefinition = "TEXT")
    @Size(min = 2, max = 90, message = "min 2 and max 90 characters are allowed")
    private String qc1Comments;
    private Date dateCreated;
    private Date lastUpdated;
    @Size(min = 2, max = 20, message = "min 2 and max 20 characters are allowed for creator name")
    private String creatorName;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Enumerated(EnumType.STRING)
    private RejectionScrap rejectionOrScrap;
    @Size(min = 2, max = 20, message = "min 2 and max 20 characters are allowed for vendor")
    private String vendor;
    @Size(min = 2, max = 20, message = "min 2 and max 20 characters are allowed for notification")
    private String notificationNumber;
    @Size(min = 2, max = 20, message = "min 2 and max 20 characters are allowed for authorizer name")
    private String authorizedBy;
    private Date qcInspectionDate;

    @PrePersist
    protected void onCreate() {
        dateCreated = new Date(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = new Date(System.currentTimeMillis());
    }
}
