package com.learn.learningarea.service;

import com.learn.learningarea.model.College;
import java.util.List;

public interface CollegeService {
    College saveCollege(College college);

    List<College> getAllColleges();

    void deleteCollege(Long id);

    List<College> searchColleges(String query);
    List<College> getLast32Days();

    List<College> getReportsByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate);
}
