package com.hitachi.kioskdesk.repository;

import com.hitachi.kioskdesk.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Shiva Created on 04/01/22
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("from Product")
    Page<Product> findAllPerPage(Pageable pageable);
}
