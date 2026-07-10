package com.learn.learningarea.repository;

import com.learn.learningarea.model.DailyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {

    @Query("SELECT dr FROM DailyReport dr WHERE dr.reportDate >= :startDate ORDER BY dr.reportDate DESC")
    List<DailyReport> findReportsLast30Days(LocalDate startDate);

    @Query("SELECT dr FROM DailyReport dr WHERE " +
            "CAST(dr.reportDate AS string) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "CAST(dr.enquiries AS string) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "CAST(dr.enrollment AS string) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "CAST(dr.revenue AS string) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<DailyReport> searchDailyReports(@Param("query") String query);

    List<DailyReport> findByReportDateBetween(LocalDate startDate, LocalDate endDate);
}
