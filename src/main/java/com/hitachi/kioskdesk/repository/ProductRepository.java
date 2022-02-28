package com.hitachi.kioskdesk.repository;

import com.hitachi.kioskdesk.domain.Product;
import com.hitachi.kioskdesk.enums.RejectionScrap;
import com.hitachi.kioskdesk.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

/**
 * Shiva Created on 04/01/22
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("from Product where status=:status order by dateCreated desc")
    List<Product> findAllByInStatus(@Param("status")Status status);

    @Query("from Product where status IN :status order by dateCreated desc")
    List<Product> findAllByInStatusList(@Param("status")List<Status> status);

    @Query(value = "select * from product order by date_created desc limit 50", nativeQuery = true)
    List<Product> findLatest50();

    @Query(value = "select * from product order by date_created desc limit :size", nativeQuery = true)
    List<Product> findLatestN(@Param("size")Integer size);

    @Query(value = "from Product where (status=:status or :status is null) AND (rejectionOrScrap=:rejectionScrap or :rejectionScrap is null) AND dateCreated >=:fromDate AND dateCreated <=:toDate order by dateCreated desc")
    List<Product> filterProducts(@Param("status")Status status, @Param("rejectionScrap") RejectionScrap rejectionScrap, @Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

}
