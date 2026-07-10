package com.learn.learningarea.service.impl;

import com.learn.learningarea.model.DailyReport;
import com.learn.learningarea.repository.DailyReportRepository;
import com.learn.learningarea.service.DailyReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class DailyReportServiceImpl implements DailyReportService {

    @Autowired
    private DailyReportRepository dailyReportRepository;

    @Override
    public List<DailyReport> getReportsLast30Days() {
        LocalDate startDate = LocalDate.now().minusDays(30);
        return dailyReportRepository.findReportsLast30Days(startDate);
    }

    @Override
    public DailyReport saveReport(DailyReport report) {
        return dailyReportRepository.save(report);
    }

    @Override
    public void deleteReport(Long id) {
        dailyReportRepository.deleteById(id);
    }

    @Override
    public DailyReport getReportById(Long id) {
        return dailyReportRepository.findById(id).orElse(null);
    }

    @Override
    public List<DailyReport> searchDailyReports(String query) {
        if (query == null || query.trim().isEmpty()) {
            return dailyReportRepository.findAll();
        }
        return dailyReportRepository.searchDailyReports(query);
    }

    @Override
    public List<DailyReport> getReportsByDateRange(LocalDate startDate, LocalDate endDate) {
        return dailyReportRepository.findByReportDateBetween(startDate, endDate);
    }
}
