package com.hitachi.kioskdesk.commandObject;

import com.hitachi.kioskdesk.enums.RejectionScrap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

/**
 * Shiva Created on 25/02/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCommand {

    private Long id;
    private String partNumber;
    private Integer quantity;
    private String model;
    private Date date;
    private String defectDescription;
    private String qc1Comments;
    private Date dateCreated;
    private Date lastUpdated;
    private String creatorName;
    private String status;
    private RejectionScrap rejectionOrScrap;
    private String vendor;
    private String notificationNumber;
    private String authorizedBy;
    private Date qcInspectionDate;
    private String partDescription;

}
