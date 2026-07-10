package com.learn.learningarea.repository;

import com.learn.learningarea.model.College;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.time.LocalDate;

@Repository
public interface CollegeRepository extends JpaRepository<College, Long> {

    @Query("SELECT c FROM College c WHERE c.createdAt >= :startDate ORDER BY c.createdAt DESC")
    List<College> findLast32Days(@Param("startDate") LocalDate startDate);

    @Query("SELECT c FROM College c WHERE " +
            "LOWER(c.collegeName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.category) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.location) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.contactPerson) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.mobileNo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.emailId) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<College> searchColleges(@Param("query") String query);

    List<College> findByCreatedAtBetween(LocalDate startDate, LocalDate endDate);
}
