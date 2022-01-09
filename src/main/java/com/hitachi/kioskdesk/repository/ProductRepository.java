package com.hitachi.kioskdesk.repository;

import com.hitachi.kioskdesk.domain.Product;
import com.hitachi.kioskdesk.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Shiva Created on 04/01/22
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("from Product where status=:status")
    List<Product> findAllByInNewStatus(@Param("status")Status status);

}
