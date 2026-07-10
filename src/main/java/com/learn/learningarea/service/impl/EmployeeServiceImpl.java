package com.learn.learningarea.service.impl;

import com.learn.learningarea.model.Employee;
import com.learn.learningarea.repository.EmployeeRepository;
import com.learn.learningarea.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import java.time.LocalDate;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Override
    public List<Employee> getLast32Days() {
        return repository.findLast32Days(LocalDate.now().minusDays(32));
    }

    @Autowired
    private EmployeeRepository repository;

    @Override
    public Employee saveEmployee(Employee employee) {
        return repository.save(employee);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return repository.findAll();
    }

    @Override
    public void deleteEmployee(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Employee> searchEmployees(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllEmployees();
        }
        return repository.searchEmployees(query);
    }

    @Override
    public List<Employee> getReportsByDateRange(LocalDate startDate, LocalDate endDate) {
        return repository.findByCreatedAtBetween(startDate, endDate);
    }
}
