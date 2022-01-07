package com.hitachi.kioskdesk.repository;

import com.hitachi.kioskdesk.domain.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Shiva Created on 07/01/22
 */
public interface ProductModelRepository extends JpaRepository<ProductModel, Long> {

    ProductModel findByModelName(String name);

}
