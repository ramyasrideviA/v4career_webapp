package com.learn.learningarea.service.impl;

import com.learn.learningarea.model.Corporate;
import com.learn.learningarea.repository.CorporateRepository;
import com.learn.learningarea.service.CorporateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import java.time.LocalDate;

@Service
public class CorporateServiceImpl implements CorporateService {
    @Override
    public List<Corporate> getLast32Days() {
        return repository.findLast32Days(LocalDate.now().minusDays(32));
    }

    @Autowired
    private CorporateRepository repository;

    @Override
    public Corporate saveCorporate(Corporate corporate) {
        return repository.save(corporate);
    }

    @Override
    public List<Corporate> getAllCorporates() {
        return repository.findAll();
    }

    @Override
    public void deleteCorporate(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Corporate> searchCorporates(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllCorporates();
        }
        return repository.searchCorporates(query);
    }

    @Override
    public List<Corporate> getReportsByDateRange(LocalDate startDate, LocalDate endDate) {
        return repository.findByCreatedAtBetween(startDate, endDate);
    }
}
