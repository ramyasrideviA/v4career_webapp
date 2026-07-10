package com.learn.learningarea.repository;

import com.learn.learningarea.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDate;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT e FROM Expense e WHERE e.expenseDate >= :startDate ORDER BY e.expenseDate DESC")
    List<Expense> findLast32Days(@Param("startDate") java.time.LocalDate startDate);

    @Query("SELECT e FROM Expense e WHERE " +
            "LOWER(e.expenseCategory) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.paidTo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.modeOfPayment) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "CAST(e.expenseDate AS string) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "CAST(e.amount AS string) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Expense> searchExpenses(@Param("query") String query);

    List<Expense> findByExpenseDateBetween(LocalDate startDate, LocalDate endDate);
}
