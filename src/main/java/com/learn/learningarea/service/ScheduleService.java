package com.learn.learningarea.service;

import com.learn.learningarea.model.Schedule;
import java.util.List;

public interface ScheduleService {
    Schedule saveSchedule(Schedule schedule);

    List<Schedule> getAllSchedules();

    void deleteSchedule(Long id);

    List<Schedule> searchSchedules(String query);
    List<Schedule> getLast32Days();

    List<Schedule> getReportsByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate);
    String generateNextBatchNo(String batchType);
}
