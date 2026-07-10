package com.learn.learningarea.service;

import com.learn.learningarea.model.Employee;
import java.util.List;

public interface EmployeeService {
    Employee saveEmployee(Employee employee);

    List<Employee> getAllEmployees();

    void deleteEmployee(Long id);

    List<Employee> searchEmployees(String query);
    List<Employee> getLast32Days();

    List<Employee> getReportsByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate);
}
