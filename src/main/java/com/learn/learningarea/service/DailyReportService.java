package com.learn.learningarea.service;

import com.learn.learningarea.model.DailyReport;
import java.util.List;

public interface DailyReportService {
    List<DailyReport> getReportsLast30Days();

    DailyReport saveReport(DailyReport report);

    void deleteReport(Long id);

    DailyReport getReportById(Long id);

    List<DailyReport> searchDailyReports(String query);

    List<DailyReport> getReportsByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate);
}
