package com.learn.learningarea.service.impl;

import com.learn.learningarea.model.Income;
import com.learn.learningarea.repository.IncomeRepository;
import com.learn.learningarea.service.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import java.time.LocalDate;

@Service
public class IncomeServiceImpl implements IncomeService {
    @Override
    public List<Income> getLast32Days() {
        return incomeRepository.findLast32Days(LocalDate.now().minusDays(32));
    }

    @Autowired
    private IncomeRepository incomeRepository;

    @Override
    public Income saveIncome(Income income) {
        return incomeRepository.save(income);
    }

    @Override
    public List<Income> getAllIncomes() {
        return incomeRepository.findAll();
    }

    @Override
    public Optional<Income> getIncomeById(Long id) {
        return incomeRepository.findById(id);
    }

    @Override
    public List<Income> searchIncomes(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllIncomes();
        }
        return incomeRepository.searchIncomes(query);
    }

    @Override
    public void deleteIncome(Long id) {
        incomeRepository.deleteById(id);
    }

    @Override
    public List<Income> getReportsByDateRange(LocalDate startDate, LocalDate endDate) {
        return incomeRepository.findByIncomeDateBetween(startDate, endDate);
    }
}
