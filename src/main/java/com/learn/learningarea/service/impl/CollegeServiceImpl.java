package com.learn.learningarea.service.impl;

import com.learn.learningarea.model.College;
import com.learn.learningarea.repository.CollegeRepository;
import com.learn.learningarea.service.CollegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import java.time.LocalDate;

@Service
public class CollegeServiceImpl implements CollegeService {
    @Override
    public List<College> getLast32Days() {
        return repository.findLast32Days(LocalDate.now().minusDays(32));
    }

    @Autowired
    private CollegeRepository repository;

    @Override
    public College saveCollege(College college) {
        return repository.save(college);
    }

    @Override
    public List<College> getAllColleges() {
        return repository.findAll();
    }

    @Override
    public void deleteCollege(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<College> searchColleges(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllColleges();
        }
        return repository.searchColleges(query);
    }

    @Override
    public List<College> getReportsByDateRange(LocalDate startDate, LocalDate endDate) {
        return repository.findByCreatedAtBetween(startDate, endDate);
    }
}
