package com.hitachi.kioskdesk.commandObject;

import com.hitachi.kioskdesk.enums.RejectionScrap;
import com.hitachi.kioskdesk.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;


/**
 * Shiva Created on 24/02/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCSVCommand {

    Status productStatus;
    Date fromDate = new java.sql.Date(System.currentTimeMillis());
    Date toDate = new java.sql.Date(System.currentTimeMillis());
    RejectionScrap rejectionScrap;
}
