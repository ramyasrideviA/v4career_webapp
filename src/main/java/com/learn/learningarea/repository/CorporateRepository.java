package com.learn.learningarea.repository;

import com.learn.learningarea.model.Corporate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.time.LocalDate;

@Repository
public interface CorporateRepository extends JpaRepository<Corporate, Long> {

    @Query("SELECT co FROM Corporate co WHERE co.createdAt >= :startDate ORDER BY co.createdAt DESC")
    List<Corporate> findLast32Days(@Param("startDate") LocalDate startDate);

    @Query("SELECT c FROM Corporate c WHERE " +
            "LOWER(c.companyName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.category) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.location) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.mobileNo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.emailId) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Corporate> searchCorporates(@Param("query") String query);

    List<Corporate> findByCreatedAtBetween(LocalDate startDate, LocalDate endDate);
}
