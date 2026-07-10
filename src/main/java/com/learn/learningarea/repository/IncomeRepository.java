package com.learn.learningarea.repository;

import com.learn.learningarea.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {

    @Query("SELECT i FROM Income i WHERE i.incomeDate >= :startDate ORDER BY i.incomeDate DESC")
    List<Income> findLast32Days(@Param("startDate") java.time.LocalDate startDate);

    @Query("SELECT i FROM Income i WHERE " +
            "LOWER(i.revenueChannel) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(i.paidBy) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(i.receivedBy) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(i.modeOfPayment) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "CAST(i.incomeDate AS string) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "CAST(i.amount AS string) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Income> searchIncomes(@Param("query") String query);

    List<Income> findByIncomeDateBetween(LocalDate startDate, LocalDate endDate);
}
