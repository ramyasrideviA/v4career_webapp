package com.learn.learningarea.service.impl;

import com.learn.learningarea.model.Expense;
import com.learn.learningarea.repository.ExpenseRepository;
import com.learn.learningarea.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import java.time.LocalDate;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    @Override
    public List<Expense> getLast32Days() {
        return expenseRepository.findLast32Days(LocalDate.now().minusDays(32));
    }

    @Autowired
    private ExpenseRepository expenseRepository;

    @Override
    public Expense saveExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    @Override
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    @Override
    public Optional<Expense> getExpenseById(Long id) {
        return expenseRepository.findById(id);
    }

    @Override
    public List<Expense> searchExpenses(String query) {
        return expenseRepository.searchExpenses(query);
    }

    @Override
    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    @Override
    public List<Expense> getReportsByDateRange(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByExpenseDateBetween(startDate, endDate);
    }
}
