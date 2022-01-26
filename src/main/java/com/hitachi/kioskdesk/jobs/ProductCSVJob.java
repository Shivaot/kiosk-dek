package com.hitachi.kioskdesk.jobs;

import com.hitachi.kioskdesk.domain.Product;
import com.hitachi.kioskdesk.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Shiva Created on 26/01/22
 */
@Slf4j
@Configuration
@EnableScheduling
public class ProductCSVJob {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private Environment environment;

    // 10:30 A.M Every day
    @Scheduled(cron = "0 30 10 * * ?")
    public void scheduleTaskUsingCronExpression() throws IOException {
        log.info("***************Products CSV job started*******************");
        writeProductsToCsv();
        log.info("***************Products CSV job Ended*******************");

    }

    private void writeProductsToCsv() throws IOException {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        List<Product> products = productRepository.findLatest50();
        File file = new File(environment.getProperty("product.backup.path")+"products_" + dateFormatter.format(new Date()) + ".csv");
        PrintWriter writer = new PrintWriter(file);
        ICsvBeanWriter csvWriter = new CsvBeanWriter(writer, CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"ID", "Part Number", "Qty", "Model", "Date", "Defect Description", "Creator Name", "Status",
                "Rejection/Scrap", "Vendor", "Notification Number", "Authorized By", "QC Inspection Date", "QC Comments"};
        String[] nameMapping = {"id", "partNumber", "quantity", "model", "date", "defectDescription", "creatorName", "status",
                "rejectionOrScrap", "vendor", "notificationNumber", "authorizedBy", "qcInspectionDate", "qc1Comments"};
        csvWriter.writeHeader(csvHeader);

        for (Product product : products) {
            csvWriter.write(product, nameMapping);
        }
        csvWriter.close();
    }
}
