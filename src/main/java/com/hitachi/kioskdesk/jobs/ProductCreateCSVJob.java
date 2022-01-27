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
import java.util.Objects;

/**
 * Shiva Created on 26/01/22
 */
@Slf4j
@Configuration
@EnableScheduling
public class ProductCreateCSVJob {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private Environment environment;

    // 10:30 A.M Every day
    @Scheduled(cron = "0 30 10 * * ?")
    public void run() throws IOException {
        log.info("***************Products CSV job started*******************");
        try {
            deleteFilesOlderThan30Days();
        } catch (Exception ex) {
            log.error("Exception while deleting files older than 30 days", ex);
        }
        writeProductsToCsv();
        log.info("***************Products CSV job Ended*******************");

    }

    private void deleteFilesOlderThan30Days() {
        File folder = new File(Objects.requireNonNull(environment.getProperty("product.backup.path")));
        long days = 30;
        String fileExtension = ".csv";

        if (folder.exists()) {
            File[] listFiles = folder.listFiles();
            long eligibleForDeletion = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000);
            if (listFiles != null) {
                for (File listFile: listFiles) {
                    if (listFile.getName().endsWith(fileExtension) && listFile.lastModified() < eligibleForDeletion) {
                        if (!listFile.delete()) {
                            System.out.println("Sorry Unable to Delete Files..");
                        }
                    }
                }
            }
        }
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
