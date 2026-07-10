package com.learn.learningarea.repository;

import com.learn.learningarea.model.Enquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;

@Repository
public interface EnquiryRepository extends JpaRepository<Enquiry, Long> {

    @Query("SELECT e FROM Enquiry e WHERE e.enquiryDate >= :startDate ORDER BY e.enquiryDate DESC")
    List<Enquiry> findLast32Days(@Param("startDate") java.time.LocalDate startDate);

    @Query("SELECT e FROM Enquiry e WHERE " +
            "LOWER(e.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.mobileNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.service) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.source) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.category) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "CAST(e.enquiryDate AS string) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Enquiry> searchEnquiries(@Param("query") String query);

    List<Enquiry> findByEnquiryDateBetween(LocalDate startDate, LocalDate endDate);
}
