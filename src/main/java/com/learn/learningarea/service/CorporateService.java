package com.learn.learningarea.service;

import com.learn.learningarea.model.Corporate;
import java.util.List;

public interface CorporateService {
    Corporate saveCorporate(Corporate corporate);

    List<Corporate> getAllCorporates();

    void deleteCorporate(Long id);

    List<Corporate> searchCorporates(String query);
    List<Corporate> getLast32Days();

    List<Corporate> getReportsByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate);
}
