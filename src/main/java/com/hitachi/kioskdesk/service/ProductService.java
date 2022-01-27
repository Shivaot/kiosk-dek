package com.hitachi.kioskdesk.service;

import com.hitachi.kioskdesk.domain.Product;
import com.hitachi.kioskdesk.enums.Status;
import com.hitachi.kioskdesk.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Shiva Created on 06/01/22
 */
@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;


    public Page<Product> findPaginated(Pageable pageable, Status status) {
        List<Product> products = new ArrayList<>();
        if (status == Status.QC) {
            products = productRepository.findAllByInNewStatus(Status.QC);
        } else if (status == Status.CANCELLED) {
            products = productRepository.findAllByInNewStatus(Status.CANCELLED);
        } else if (status == Status.NEW) {
            products = productRepository.findAllByInNewStatus(Status.NEW);
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
}
