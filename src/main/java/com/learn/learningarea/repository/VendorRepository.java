package com.learn.learningarea.repository;

import com.learn.learningarea.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.time.LocalDate;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {

    @Query("SELECT v FROM Vendor v WHERE v.createdAt >= :startDate ORDER BY v.createdAt DESC")
    List<Vendor> findLast32Days(@Param("startDate") LocalDate startDate);
    @Query("SELECT v FROM Vendor v WHERE " +
            "LOWER(v.vendorName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(v.companyName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(v.category) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(v.location) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(v.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Vendor> searchVendors(@Param("query") String query);

    List<Vendor> findByCreatedAtBetween(LocalDate startDate, LocalDate endDate);
}
