package com.learn.learningarea.service.impl;

import com.learn.learningarea.model.Schedule;
import com.learn.learningarea.repository.ScheduleRepository;
import com.learn.learningarea.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import java.time.LocalDate;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Override
    public List<Schedule> getLast32Days() {
        return repository.findLast32Days(LocalDate.now().minusDays(32));
    }

    @Autowired
    private ScheduleRepository repository;

    @Override
    public Schedule saveSchedule(Schedule schedule) {
        return repository.save(schedule);
    }

    @Override
    public List<Schedule> getAllSchedules() {
        return repository.findAll();
    }

    @Override
    public void deleteSchedule(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Schedule> searchSchedules(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllSchedules();
        }
        return repository.searchSchedules(query);
    }

    @Override
    public List<Schedule> getReportsByDateRange(LocalDate startDate, LocalDate endDate) {
        return repository.findByStartDateBetween(startDate, endDate);
    }

    @Override
    public String generateNextBatchNo(String batchType) {
        String prefix = "";
        if ("Week Days".equalsIgnoreCase(batchType)) {
            prefix = "BWD-";
        } else if ("Weekend".equalsIgnoreCase(batchType)) {
            prefix = "BWE-";
        } else {
            return ""; 
        }
        
        List<Schedule> schedules = repository.findByBatchTypeOrderByBatchNoDesc(batchType);
        String latestBatchNo = null;
        if (schedules != null && !schedules.isEmpty()) {
            latestBatchNo = schedules.get(0).getBatchNo();
        }

        if (latestBatchNo != null && latestBatchNo.startsWith(prefix)) {
            try {
                int number = Integer.parseInt(latestBatchNo.substring(prefix.length()));
                return prefix + String.format("%04d", number + 1);
            } catch (NumberFormatException e) {
                return prefix + "0001";
            }
        } else {
            return prefix + "0001";
        }
    }
}
