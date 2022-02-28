package com.hitachi.kioskdesk.service;

import com.hitachi.kioskdesk.commandObject.ProductCommand;
import com.hitachi.kioskdesk.domain.Product;
import com.hitachi.kioskdesk.enums.RejectionScrap;
import com.hitachi.kioskdesk.enums.Status;
import com.hitachi.kioskdesk.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.sql.Date;
import java.util.*;

/**
 * Shiva Created on 06/01/22
 */
@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;


    public Page<Product> findPaginated(Pageable pageable, Status status) {
        List<Product> products;
        if (status == Status.QC) {
            products = productRepository.findAllByInStatusList(Arrays.asList(Status.QC, Status.NEW));
        } else if (status == Status.CANCELLED) {
            products = productRepository.findAllByInStatus(Status.CANCELLED);
        } else if (status == Status.NEW) {
            products = productRepository.findAllByInStatus(Status.NEW);
        } else {
            products = productRepository.findAll(Sort.by(Sort.Direction.DESC, "dateCreated"));
        }

        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Product> list;

        if (products.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, products.size());
            list = products.subList(startItem, toIndex);
        }

        Page<Product> productPage
                = new PageImpl<Product>(list, PageRequest.of(currentPage, pageSize), products.size());

        return productPage;
    }

    public byte[] getProductCSV(String status, String rejectionScrap, Date fromDate, Date toDate) throws IOException {
        Status statusEnum = Status.valueOfLabel(status);
        RejectionScrap rejectionScrapEnum = RejectionScrap.valueOfLabel(rejectionScrap);
        List<Product> products = productRepository.filterProducts(statusEnum, rejectionScrapEnum, fromDate, toDate);
        return getCSVBytes(products);
    }

    private byte[] getCSVBytes(List<Product> products) throws IOException {
        File file = null;
        byte[] bytes = new byte[0];
        try {
            file = File.createTempFile(UUID.randomUUID().toString(), ".csv");
            writeProductsToCSV(products, file);
            bytes = Files.readAllBytes(file.toPath());
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        } finally {
            Files.delete(file.toPath());
        }
        return bytes;
    }

    public void writeProductsToCSV(List<Product> products, File file) throws IOException {
        PrintWriter writer = new PrintWriter(file);
        ICsvBeanWriter csvWriter = new CsvBeanWriter(writer, CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"ID", "Part Number", "Qty", "Model", "Date", "Defect Description", "Creator Name", "Status",
                "Rejection/Scrap", "Vendor", "Notification Number", "Authorized By", "QC Inspection Date", "QC Comments", "Part Desc"};
        String[] nameMapping = {"id", "partNumber", "quantity", "model", "date", "defectDescription", "creatorName", "status",
                "rejectionOrScrap", "vendor", "notificationNumber", "authorizedBy", "qcInspectionDate", "qc1Comments", "partDescription"};
        csvWriter.writeHeader(csvHeader);

        ModelMapper modelMapper = new ModelMapper();

        for (Product product : products) {
            ProductCommand productCommand = modelMapper.map(product, ProductCommand.class);
            productCommand.setStatus(product.getStatus().getDisplayValue());
            csvWriter.write(productCommand, nameMapping);
        }
        csvWriter.close();
    }
}
