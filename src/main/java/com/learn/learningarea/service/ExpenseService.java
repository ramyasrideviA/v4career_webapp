package com.learn.learningarea.service;

import com.learn.learningarea.model.Expense;
import java.util.List;
import java.util.Optional;

public interface ExpenseService {
    Expense saveExpense(Expense expense);
    List<Expense> getAllExpenses();
    Optional<Expense> getExpenseById(Long id);
    List<Expense> searchExpenses(String query);
    List<Expense> getLast32Days();
    void deleteExpense(Long id);

    List<com.learn.learningarea.model.Expense> getReportsByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate);
}
