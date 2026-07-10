package com.learn.learningarea.service;

import com.learn.learningarea.model.Income;
import java.util.List;
import java.util.Optional;

public interface IncomeService {
    Income saveIncome(Income income);
    List<Income> getAllIncomes();
    Optional<Income> getIncomeById(Long id);
    List<Income> searchIncomes(String query);
    List<Income> getLast32Days();
    void deleteIncome(Long id);

    List<com.learn.learningarea.model.Income> getReportsByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate);
}
